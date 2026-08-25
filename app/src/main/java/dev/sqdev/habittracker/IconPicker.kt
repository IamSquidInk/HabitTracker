package dev.sqdev.habittracker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class IconCategory(val label: String, val icons: List<String>)

val ICON_CATEGORIES = listOf(
    IconCategory("Activities", listOf("🏃‍➡️", "💃", "🧗", "🧘", "🏇", "🏌️", "🏄", "🚣", "🏊‍♀️", "🤽‍♀️", "🤾‍♀️", "⛹️", "🏋️", "🚴", "🚵", "🤸", "🤼")),
    IconCategory("Sports", listOf("⚽", "⚾", "🥎", "🏀", "🏐", "🏈", "🏉", "🎱", "🎳", "🥌", "⛳", "⛸️", "🎣", "🤿", "🛶", "🎿", "🏒", "🏓", "🏸", "🎾", "🥏", "🎯", "🥊", "🥋")),
    IconCategory("Games", listOf("🎮", "🕹️", "🎲", "🧩")),
    IconCategory("Creative", listOf("🎼", "🎙️", "🎤", "📯", "🥁", "🪘", "🪇", "🪈", "🪉", "🎷", "🪗", "🎺", "🎸", "🪕", "🎻", "🎹")),
    IconCategory("Tools", listOf("🪓", "🔨", "⛏️", "⚒️", "🛠️", "🔧", "🪛", "🔩", "⚗️", "🧪", "🧬", "🩺", "🧫", "💉", "🔬", "🔭")),

    IconCategory("Work, Study & Creative", listOf("💻", "🖥️", "📷", "📽️", "👩‍💻", "📚", "📑", "📖", "✏️", "✒️", "🖋️", "🖊️", "🖍️", "📝", "🎓", "🧠", "🖌️", "🎨", "🫟", "🖼️",)),

    IconCategory("Food", listOf("🍕", "🍔", "🍟", "🌭", "🍿", "🥓", "🥚", "🧇", "🥞", "🍞", "🥐", "🥖", "🫓", "🧀", "🥗", "🥙", "🥪", "🌮", "🌯", "🫔", "🥫", "🍖", "🍗", "🍠", "🥟", "🥠", "🥡", "🍱", "🍘", "🍙", "🍚", "🍛", "🍜", "🦪", "🍣", "🍤", "🍥", "🥮", "🍢", "🧆", "🥘", "🍲", "🫕", "🍝")),
    IconCategory("Dessert", listOf("🥨", "🥯", "🥧", "🍦", "🍧", "🍨", "🍩", "🍪", "🎂", "🍰", "🧁", "🍫", "🍬", "🍭", "🍡", "🍮", "🍯")),
    IconCategory("Drinks", listOf("🥛", "🧃", "☕", "🫖", "🍵", "🧉", "🍶", "🍾", "🍷", "🍸", "🍹", "🍺", "🍻", "🥂", "🥃", "🥤", "🧋" )),
    IconCategory("Fruit", listOf("🥝", "🥥", "🍇", "🍈", "🍉", "🍊", "🍋" , "🍍", "🥭", "🍎", "🍏", "🍐", "🍑", "🍒", "🍓", "🫐", "🍅", "🫒", "🍆", "🌽", "🌶️", "🫑", "🥑")),
    IconCategory("Vegetable", listOf("🥬", "🥒", "🥦", "🥔", "🧄", "🧅", "🥕", "🌰", "🫚", "🫛", "🍄‍", "🫜", "🥜", "🫘")),

    IconCategory("Chores & Health", listOf("🧹", "🧺", "🛒", "💤", "🧴", "💊")),
    IconCategory("Other", listOf("🌱", "🐾", "🎮", "✈️", "💰", "📌"))
)

@Composable
fun IconPickerDialog(
    onIconSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose an icon") },
        text = {
            LazyColumn(modifier = Modifier.height(360.dp)) {
                ICON_CATEGORIES.forEach { section ->
                    item {
                        Text(
                            text = section.label,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(6),
                            modifier = Modifier.height((60 * ((section.icons.size + 5) / 6)).dp)
                        ) {
                            items(section.icons) { icon ->
                                TextButton(onClick = {
                                    onIconSelected(icon)
                                    onDismiss()
                                }) {
                                    Text(icon, style = MaterialTheme.typography.headlineSmall)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}