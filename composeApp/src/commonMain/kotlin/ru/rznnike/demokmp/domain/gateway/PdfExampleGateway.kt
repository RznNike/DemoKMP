package ru.rznnike.demokmp.domain.gateway

import java.io.File

interface PdfExampleGateway {
    suspend fun getSamplePdf(): File
}