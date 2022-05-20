package com.rivada.rdme.model



data class PayLoadModel(
    val payload: Payload
)

data class Payload (
    val cells: List<Cell>,
    val video: Video,
    val home:Home,
    val signalqualitycolors: Signalqualitycolors

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
    var getSsRsrp: Int= -1,
    var getSsRsrq: Int = -1,
    var getSsSinr: Int=-1,
)
data class Signalqualitycolors(
    var green:String? =null,
    var blue:String?=null,
    var red:String?=null,
    var yellow:String?=null,
    var black:String?=null
)