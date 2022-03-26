package com.example.lightmediator

import java.io.Serializable

class User (val name : String, val id : Int) {



}

/*
@Entity
data class User (
    @ColumnInfo(name= "name") val name : String,
    @ColumnInfo(id= "id") val userId : Int) : Serializable{
    @PrimaryKey(autoGenerate = true) var id: Int? = null



}
*/