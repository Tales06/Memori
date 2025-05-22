package com.example.memori.data

data class NoteObject(
    var note: String,
    var pathImg: String? = null,
    var checkList: List<CheckList>? = null,
)

