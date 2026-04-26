package com.java.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.java.myapplication.ui.components.ChapterRangeFields
import com.java.myapplication.ui.components.DotBadge
import com.java.myapplication.ui.components.SectionTitle
import com.java.myapplication.ui.components.buildRangeSummary
import com.java.myapplication.ui.model.ExportRecord

@Composable
fun ExportScreen() {
    val records = listOf(
        ExportRecord("night_tide_rewrite_v1.txt", "按章节合并导出 · 2026-04-26", "成功"),
        ExportRecord("night_tide_ch03.txt", "单章导出 · 2026-04-25", "成功"),
        ExportRecord("batch_preview.txt", "批量预览导出 · 2026-04-24", "待确认")
    )
    var startChapter by remember { mutableStateOf("1") }
    var endChapter by remember { mutableStateOf("12") }
    val rangeSummary = buildRangeSummary(startChapter, endChapter)

    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SectionTitle("导出与交付", "按章节范围导出，更符合批量处理后的交付习惯；后续可直接联动真实导出任务。")
        }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("导出目标：TXT", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    ChapterRangeFields(
                        startChapter = startChapter,
                        endChapter = endChapter,
                        onStartChange = { startChapter = it.filter(Char::isDigit) },
                        onEndChange = { endChapter = it.filter(Char::isDigit) }
                    )
                    Text("当前导出范围：$rangeSummary", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        DotBadge("合并导出", MaterialTheme.colorScheme.primary)
                        DotBadge("含章节标题", MaterialTheme.colorScheme.secondary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {}) { Text("导出该范围") }
                        OutlinedButton(onClick = {}) { Text("导出当前章节") }
                    }
                }
            }
        }
        items(records) { record ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                        Text(record.fileName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(record.detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    DotBadge(record.state, if (record.state == "成功") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary)
                }
            }
        }
    }
}