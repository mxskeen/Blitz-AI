package com.krishna.blitzai.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.krishna.blitzai.database.entity.ChatMessage

@Composable
fun Message(
    message: ChatMessage,
    needTitle: Boolean = true,
    actions: List<MessageAction> = emptyList()
) {
    BaseMessage(
        message = message,
        needTitle = needTitle,
        actions = actions
    ) { modifier, expanded ->
        val textSelectionsColors = if (message.fromBlitz)
            LocalTextSelectionColors.current
        else TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.primary,
            backgroundColor = Color.DarkGray.copy(alpha = 0.4f)
        )

        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionsColors) {
            if (!expanded) {
                SelectionContainer { // maybe google will make internal SelectionContainer with access to selection public
                    MessageText(
                        modifier = modifier,
                        message = message
                    )
                }
            } else {
                MessageText(
                    modifier = modifier,
                    message = message
                )
            }
        }
    }
}

@Composable
private fun MessageText(modifier: Modifier, message: ChatMessage) {
    val textColor = if (message.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer

    if (message.fromBlitz) {
        MarkdownMessage(
            modifier = modifier.messageBubble(message),
            text = message.content.orEmpty(),
            color = textColor,
            generating = message.generating,
            messageId = message.id
        )
    } else {
        Text(
            modifier = modifier.messageBubble(message),
            text = message.content ?: "",
            color = textColor
        )
    }
}

@Composable
fun TypingMessage(needTitle: Boolean = true) {
    val message = ChatMessage(role = "assistant")

    BaseMessage(
        message = message,
        needTitle = needTitle
    ) { _, expanded ->
        PulsatingDots(
            modifier = Modifier
                .messageBubble(message)
                .padding(5.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BaseMessage(
    message: ChatMessage,
    needTitle: Boolean = true,
    actions: List<MessageAction> = emptyList(),
    content: @Composable ColumnScope.(Modifier, Boolean) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.size(if (message.fromUser) 20.dp else 5.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = if (message.fromUser) Alignment.End else Alignment.Start
        ) {
            if (needTitle) {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = stringResource(id = message.parsedRole),
                    style = MaterialTheme.typography.labelSmall
                )

                Spacer(modifier = Modifier.size(3.dp))
            }

            content(
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expanded = !expanded },
                expanded
            )

            if (actions.isNotEmpty()) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        actions.forEach { ActionChip(messageAction = it) { expanded = false } }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(if (message.fromBlitz) 20.dp else 5.dp))
    }
}

@Composable
private fun ActionChip(messageAction: MessageAction, onDismiss: () -> Unit) {
    val tint = messageAction.tint ?: MaterialTheme.colorScheme.primary

    AssistChip(
        onClick = {
            messageAction.onClick()
            onDismiss()
        },
        label = { Text(text = stringResource(id = messageAction.title)) },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = messageAction.icon),
                contentDescription = null
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            labelColor = tint,
            leadingIconContentColor = tint
        ),
        border = AssistChipDefaults.assistChipBorder(borderColor = tint)
    )
}

fun Modifier.messageBubble(message: ChatMessage, picture: Boolean = false) = composed {
    background(
        color = if (message.fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
        shape = messageCornerShape(message)
    ).padding(
        horizontal = if (picture) 0.dp else 10.dp,
        vertical = if (picture) 0.dp else 7.dp
    )
}

private fun messageCornerShape(message: ChatMessage) = RoundedCornerShape(
    20.dp,
    20.dp,
    if (message.fromUser) 5.dp else 20.dp,
    if (message.fromBlitz) 5.dp else 20.dp
)

data class MessageAction(
    val title: Int,
    val icon: Int,
    val tint: Color? = null,
    val onClick: () -> Unit
)

@Composable
private fun MarkdownMessage(modifier: Modifier, text: String, color: Color, generating: Boolean = false, messageId: Long = 0) {
    val blocks = remember(text) { parseMarkdownBlocks(text) }
    Column(modifier = modifier) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Code -> {
                    CodeBlock(content = block.content, textColor = color)
                    Spacer(Modifier.size(6.dp))
                }
                is MarkdownBlock.Paragraph -> {
                    Text(
                        text = block.styled,
                        color = color
                    )
                    Spacer(Modifier.size(2.dp))
                }
                is MarkdownBlock.Think -> {
                    ThinkBlock(block = block, generating = generating, color = color, messageId = messageId)
                    Spacer(Modifier.size(6.dp))
                }
            }
        }
    }
}

private sealed class MarkdownBlock {
    data class Paragraph(val styled: AnnotatedString): MarkdownBlock()
    data class Code(val content: String): MarkdownBlock()
    data class Think(val content: String): MarkdownBlock()
}

private fun parseMarkdownBlocks(input: String): List<MarkdownBlock> {
    // Very small subset: fenced code blocks ``` ``` and inline styling for **bold**, *italic*, `code`, and # headings
    val blocks = mutableListOf<MarkdownBlock>()
    // Extract <think>...</think> sections first, but keep ordering
    val thinkRegex = Regex("(?s)<think>(.*?)</think>")
    var remaining = input
    var lastIndex = 0
    thinkRegex.findAll(input).forEach { m ->
        val before = input.substring(lastIndex, m.range.first)
        if (before.isNotBlank()) blocks.addAll(parseMarkdownBlocksNoThink(before))
        val inner = m.groupValues[1]
        blocks.add(MarkdownBlock.Think(inner.trim()))
        lastIndex = m.range.last + 1
    }
    if (lastIndex < input.length) {
        remaining = input.substring(lastIndex)
        if (remaining.isNotBlank()) blocks.addAll(parseMarkdownBlocksNoThink(remaining))
    }
    return blocks
}

private fun parseMarkdownBlocksNoThink(input: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = input.lines()
    val codeBuffer = StringBuilder()
    var inCode = false

    fun flushParagraphBuffer(buffer: MutableList<String>) {
        if (buffer.isNotEmpty()) {
            val paragraph = buffer.joinToString("\n")
            blocks.add(MarkdownBlock.Paragraph(styleInline(paragraph)))
            buffer.clear()
        }
    }

    val paraBuffer = mutableListOf<String>()
    for (raw in lines) {
        val line = raw.rstrip()
        if (line.startsWith("```")) {
            if (inCode) {
                // closing fence
                blocks.add(MarkdownBlock.Code(codeBuffer.toString().trimEnd()))
                codeBuffer.setLength(0)
                inCode = false
            } else {
                // opening fence
                flushParagraphBuffer(paraBuffer)
                inCode = true
            }
            continue
        }
        if (inCode) {
            codeBuffer.appendLine(raw)
        } else {
            if (line.isBlank()) {
                flushParagraphBuffer(paraBuffer)
            } else {
                paraBuffer.add(line)
            }
        }
    }
    if (codeBuffer.isNotEmpty()) {
        blocks.add(MarkdownBlock.Code(codeBuffer.toString().trimEnd()))
    }
    flushParagraphBuffer(paraBuffer)
    return blocks
}

private fun styleInline(text: String): AnnotatedString = buildAnnotatedString {
    // Headings (#, ##, ###) -> bold, slightly emphasized
    val headingMatch = Regex("^(#{1,6})\\s+(.*)").find(text)
    val content = if (headingMatch != null) headingMatch.groupValues[2] else text
    val baseStyle = if (headingMatch != null) SpanStyle(fontWeight = FontWeight.SemiBold) else SpanStyle()

    appendStyled(content, baseStyle)
}

private fun AnnotatedString.Builder.appendStyled(text: String, base: SpanStyle) {
    // Apply inline transformations in order: code ``, bold **, italic *
    var i = 0
    while (i < text.length) {
        when {
            text.startsWith("`", i) -> {
                val end = text.indexOf('`', i + 1)
                if (end > i) {
                    withStyle(base.merge(SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x22000000)) )) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else {
                    append(text.substring(i))
                    break
                }
            }
            text.startsWith("**", i) -> {
                val end = text.indexOf("**", i + 2)
                if (end > i) {
                    withStyle(base.merge(SpanStyle(fontWeight = FontWeight.Bold))) {
                        append(text.substring(i + 2, end))
                    }
                    i = end + 2
                } else {
                    append(text.substring(i))
                    break
                }
            }
            text.startsWith("*", i) -> {
                val end = text.indexOf('*', i + 1)
                if (end > i) {
                    withStyle(base.merge(SpanStyle(fontStyle = FontStyle.Italic))) {
                        append(text.substring(i + 1, end))
                    }
                    i = end + 1
                } else {
                    append(text.substring(i))
                    break
                }
            }
            else -> {
                // append until next special
                val next = sequenceOf(
                    text.indexOf('`', i).takeIf { it >= 0 } ?: Int.MAX_VALUE,
                    text.indexOf("**", i).takeIf { it >= 0 } ?: Int.MAX_VALUE,
                    text.indexOf('*', i).takeIf { it >= 0 } ?: Int.MAX_VALUE
                ).minOrNull() ?: Int.MAX_VALUE
                val end = if (next == Int.MAX_VALUE) text.length else next
                withStyle(base) { append(text.substring(i, end)) }
                i = end
            }
        }
    }
}

@Composable
private fun CodeBlock(content: String, textColor: Color) {
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    val ctx = LocalContext.current
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
            .fillMaxWidth()
    ) {
        Text(
            text = content.trimEnd(),
            color = textColor,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(12.dp)
        )
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
            onClick = {
                clipboard.setText(AnnotatedString(content))
                Toast.makeText(ctx, "Copied", Toast.LENGTH_SHORT).show()
            }
        ) {
            Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = "Copy code", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ThinkBlock(block: MarkdownBlock.Think, generating: Boolean, color: Color, messageId: Long) {
    var expanded by remember(messageId, generating) { mutableStateOf(generating) }
    // Auto-collapse when generation stops
    LaunchedEffect(generating) {
        if (!generating) expanded = false
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (generating) "Thinking…" else "Thought",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (expanded) "Hide" else "Show",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
        if (expanded) {
            Spacer(Modifier.size(6.dp))
            Text(
                text = buildAnnotatedString { append(block.content) },
                color = color
            )
        }
    }
}

private fun String.rstrip(): String {
    var end = this.length
    while (end > 0 && this[end - 1].isWhitespace() && this[end - 1] != '\n') end--
    return this.substring(0, end)
}