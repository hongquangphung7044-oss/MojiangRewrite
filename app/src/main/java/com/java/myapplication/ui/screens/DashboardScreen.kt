package com.java.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Hub
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.java.myapplication.ui.components.GradientHeroCard
import com.java.myapplication.ui.components.SectionTitle
import com.java.myapplication.ui.model.DashboardStat

@Composable
fun DashboardScreen() {
    val stats = listOf(
        DashboardStat("当前项目", "1 个", "夜潮纪事·加料版"),
        DashboardStat("章节总量", "24 章", "待处理 6 章"),
        DashboardStat("模型配置", "4 套", "1 套默认启用"),
        DashboardStat("导出记录", "3 次", "最近 1 次成功")
    )

    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            GradientHeroCard(
                title = "墨匠 Rewrite",
                subtitle = "长篇小说改写与加料工作台",
                footnote = "从模型、提示词到导出，聚焦可执行流程。"
            )
        }
        item {
            SectionTitle("工作概览", "当前状态一屏查看。")
        }
        items(stats) { stat ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(stat.title, style = MaterialTheme.typography.titleMedium)
                        Text(stat.caption, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(stat.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            SectionTitle("快捷入口", "保留必要说明，减少示范性文案。")
        }
        item {
            FeatureRow(Icons.Rounded.AutoStories, "导入原文", "导入 TXT 并整理章节")
        }
        item {
            FeatureRow(Icons.Rounded.Bolt, "开始加料", "调强度、模板和输出方向")
        }
        item {
            FeatureRow(Icons.Rounded.Hub, "模型配置", "保存多套提供商与模型方案")
        }
        item {
            FeatureRow(Icons.Rounded.Settings, "导出结果", "导出章节或整书文本")
        }
    }
}

@Composable
private fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}