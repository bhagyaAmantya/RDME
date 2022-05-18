package com.rivada.rdme.model



data class PayLoadModel(
    val payload: Payload
)

data class Payload (
    val cells: List<Cell>,
    val video: Video,
    val home:Home

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
data class Home(
    val networkname:String,
    val color:String
)
data class SignalData(
    val getSsRsrp: Int,
    val getSsRsrq: Int,
    val getSsSinr: Int,
)