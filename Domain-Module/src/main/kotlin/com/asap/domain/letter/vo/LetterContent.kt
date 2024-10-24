package com.asap.domain.letter.vo

data class LetterContent(
    var content: String,
    val images: MutableList<String>,
    var templateType: Int,
) {
    fun updateContent(content: String) {
        this.content = content
    }

    fun updateImages(images: MutableList<String>) {
        this.images.clear()
        this.images.addAll(images)
    }

    fun updateTemplateType(templateType: Int) {
        this.templateType = templateType
    }

    fun delete() {
        this.content = ""
        this.images.clear()
    }
}
