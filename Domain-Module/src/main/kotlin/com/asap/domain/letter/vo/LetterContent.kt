package com.asap.domain.letter.vo

data class LetterContent(
    var content: String,
    val images: MutableList<String>,
    val templateType: Int,
) {
    fun updateContent(content: String) {
        this.content = content
    }

    fun updateImages(images: MutableList<String>) {
        this.images.clear()
        this.images.addAll(images)
    }

    fun delete()  {
        this.content = ""
        this.images.clear()
    }
}
