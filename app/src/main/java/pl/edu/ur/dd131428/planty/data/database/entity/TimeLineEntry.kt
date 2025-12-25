package pl.edu.ur.dd131428.planty.data.database.entity

data class TimelineEntry(
    val title: String,
    val date: Long,
    val photos: List<String>
)