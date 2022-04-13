package com.rivada.rdme.model



data class PayLoadModel(
    val payload: Payload
)

data class Payload (
    val cells: List<Cell>,
    val video: Video

)
data class Cell(
    val cellname: String,
    val color: String,
    val id: String
)
data class Video(
    val description: String,
    val highUrl: String,
    val lowUrl: String,
    val showvideo: String,
    val url: String
)