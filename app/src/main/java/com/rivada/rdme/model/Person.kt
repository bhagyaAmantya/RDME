package com.rivada.rdme.model



//data class Person(val personItem:List<PersonItem>)

data class PersonItem(
    val age: Int,
    val messages: List<String>,
    val name: String,
    val videos: Videos
)

data class Videos( var description : String? ,
                   var sources     : ArrayList<String>,
                   var subtitle    : String? ,
                   var thumb       : String? ,
                   var title       : String?
)


