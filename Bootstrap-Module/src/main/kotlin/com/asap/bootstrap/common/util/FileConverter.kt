package com.asap.bootstrap.common.util

import com.asap.common.file.FileMetaData
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class FileConverter {

    fun convert(multipartFile: MultipartFile): FileMetaData{
        return FileMetaData(
            name = multipartFile.originalFilename!!,
            size = multipartFile.size,
            contentType = multipartFile.contentType!!,
            inputStream = { multipartFile.bytes }
        )
    }
}