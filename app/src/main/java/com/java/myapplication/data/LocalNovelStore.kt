package com.java.myapplication.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.util.concurrent.atomic.AtomicLong

/**
 * 轻量本地仓库：先保证 APK 交互闭环和稳定性。
 * 后续可替换为 Room/DataStore，不影响 UI 调用层。
 */
object LocalNovelStore {
    private val idGen = AtomicLong(1)

    val novels = mutableStateListOf<NovelProject>()
    val promptTemplates = mutableStateListOf<PromptTemplate>()
    val activeNovelId = mutableStateOf<Long?>(null)
    val statusMessage = mutableStateOf("尚未导入小说")

    init {
        promptTemplates += PromptTemplate(
            id = idGen.getAndIncrement(),
            title = "通用加料模板",
            content = "在不破坏原剧情主线的前提下，加强环境、动作、心理、氛围和情绪推进。保持角色性格稳定，保留原有人名、地名和关键事件。"
        )
        promptTemplates += PromptTemplate(
            id = idGen.getAndIncrement(),
            title = "文风一致模板",
            content = "模仿原文叙述节奏和用词习惯，不要突然改变视角、人设和时代背景。补充细节时优先服务剧情连贯性。"
        )
    }

    val activeNovel: NovelProject?
        get() = novels.firstOrNull { it.id == activeNovelId.value }

    fun importTxt(context: Context, uri: Uri) {
        runCatching {
            val name = queryDisplayName(context, uri).ifBlank { "未命名.txt" }
            val text = context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader(Charsets.UTF_8).use { it.readText() }
            }.orEmpty()
            require(text.isNotBlank()) { "文件内容为空" }
            require(text.length <= 2_000_000) { "文件过大，请先拆分后导入，当前稳定版建议单次不超过约200万字" }

            val novelId = idGen.getAndIncrement()
            val chapters = splitChapters(text).mapIndexed { index, chapter ->
                ChapterData(
                    id = idGen.getAndIncrement(),
                    novelId = novelId,
                    index = index + 1,
                    title = chapter.first,
                    originalContent = chapter.second,
                    rewrittenContent = "",
                    status = "待加料"
                )
            }
            val novel = NovelProject(
                id = novelId,
                title = name.removeSuffix(".txt").removeSuffix(".TXT"),
                fileName = name,
                chapters = chapters.toMutableList()
            )
            novels.add(0, novel)
            activeNovelId.value = novelId
            statusMessage.value = "导入完成：${novel.title} · ${chapters.size} 章"
        }.onFailure {
            statusMessage.value = "导入失败：${it.message ?: "未知错误"}"
        }
    }

    fun upsertPrompt(id: Long?, title: String, content: String) {
        val safeTitle = title.trim().ifBlank { "未命名模板" }
        val safeContent = content.trim().ifBlank { "请在这里填写提示词内容。" }
        val index = id?.let { promptTemplates.indexOfFirst { p -> p.id == it } } ?: -1
        if (index >= 0) {
            promptTemplates[index] = promptTemplates[index].copy(title = safeTitle, content = safeContent)
        } else {
            promptTemplates.add(0, PromptTemplate(idGen.getAndIncrement(), safeTitle, safeContent))
        }
    }

    fun deletePrompt(id: Long) {
        if (promptTemplates.size <= 1) {
            statusMessage.value = "至少保留一个提示词模板"
            return
        }
        promptTemplates.removeAll { it.id == id }
    }

    fun applyFakeRewrite(chapterIds: List<Long>, prompt: String, intensity: Int) {
        val novel = activeNovel ?: return
        var changed = 0
        for (i in novel.chapters.indices) {
            val chapter = novel.chapters[i]
            if (chapter.id in chapterIds) {
                novel.chapters[i] = chapter.copy(
                    rewrittenContent = buildString {
                        appendLine("【加料后】${chapter.title}")
                        appendLine("【强度】$intensity%")
                        appendLine("【使用提示词】")
                        appendLine(prompt.take(300))
                        appendLine()
                        append(chapter.originalContent)
                        appendLine()
                        appendLine()
                        appendLine("——以上为本地预览占位内容。接入 API 后这里会显示真实模型加料结果。")
                    },
                    status = "已加料"
                )
                changed++
            }
        }
        statusMessage.value = "处理完成：$changed 章，可在章节详情查看加料后内容"
    }

    private fun queryDisplayName(context: Context, uri: Uri): String {
        return runCatching {
            context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                if (c.moveToFirst()) c.getString(0) else null
            }
        }.getOrNull() ?: uri.lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':') ?: "未命名.txt"
    }

    private fun splitChapters(text: String): List<Pair<String, String>> {
        val normalized = text.replace("\r\n", "\n").replace("\r", "\n")
        val regex = Regex("""(?m)^\s*(第[零一二三四五六七八九十百千万\d]+[章节回卷集部].{0,50}|Chapter\s+\d+.{0,50}|CHAPTER\s+\d+.{0,50})\s*$""")
        val markers = regex.findAll(normalized).toList()
        if (markers.isEmpty()) {
            return normalized.chunked(6000).mapIndexed { i, part -> "自动分块 ${i + 1}" to part }
        }
        return markers.mapIndexedNotNull { i, m ->
            val start = m.range.first
            val end = if (i + 1 < markers.size) markers[i + 1].range.first else normalized.length
            val content = normalized.substring(start, end).trim()
            if (content.isBlank()) null else m.value.trim().take(80) to content
        }.ifEmpty { listOf("全文" to normalized) }
    }
}

data class NovelProject(
    val id: Long,
    val title: String,
    val fileName: String,
    val chapters: MutableList<ChapterData>
)

data class ChapterData(
    val id: Long,
    val novelId: Long,
    val index: Int,
    val title: String,
    val originalContent: String,
    val rewrittenContent: String,
    val status: String
) {
    val wordCount: Int get() = originalContent.length
}

data class PromptTemplate(
    val id: Long,
    val title: String,
    val content: String
)
