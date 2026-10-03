package com.fieldops.controller;

import com.fieldops.dto.DocumentResponse;
import com.fieldops.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @Test
    void uploadForEquipment() throws Exception {
        DocumentResponse response = createResponse();
        response.setEquipmentId(1L);
        response.setDocumentType("MANUAL");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "manual.pdf",
                "application/pdf",
                "pdf content".getBytes()
        );

        when(documentService.uploadForEquipment(
                eq(1L),
                any(),
                eq("MANUAL")
        )).thenReturn(response);

        mockMvc.perform(
                multipart("/api/documents/equipment/1")
                        .file(file)
                        .param("documentType", "MANUAL")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.fileName").value("manual.pdf"))
                .andExpect(jsonPath("$.documentType").value("MANUAL"))
                .andExpect(jsonPath("$.equipmentId").value(1));
    }

    @Test
    void uploadForWorkOrder() throws Exception {
        DocumentResponse response = createResponse();
        response.setWorkOrderId(5L);
        response.setDocumentType("MAINTENANCE_REPORT");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "maintenance.pdf",
                "application/pdf",
                "pdf content".getBytes()
        );

        when(documentService.uploadForWorkOrder(
                eq(5L),
                any(),
                eq("MAINTENANCE_REPORT")
        )).thenReturn(response);

        mockMvc.perform(
                multipart("/api/documents/work-order/5")
                        .file(file)
                        .param("documentType", "MAINTENANCE_REPORT")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.fileName").value("manual.pdf"))
                .andExpect(jsonPath("$.documentType")
                        .value("MAINTENANCE_REPORT"))
                .andExpect(jsonPath("$.workOrderId").value(5));
    }

    @Test
    void getEquipmentDocuments() throws Exception {
        DocumentResponse response = createResponse();
        response.setEquipmentId(1L);

        when(documentService.getEquipmentDocuments(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(
                get("/api/documents/equipment/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].fileName").value("manual.pdf"))
                .andExpect(jsonPath("$[0].equipmentId").value(1));
    }

    @Test
    void getWorkOrderDocuments() throws Exception {
        DocumentResponse response = createResponse();
        response.setWorkOrderId(5L);

        when(documentService.getWorkOrderDocuments(5L))
                .thenReturn(List.of(response));

        mockMvc.perform(
                get("/api/documents/work-order/5")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].fileName").value("manual.pdf"))
                .andExpect(jsonPath("$[0].workOrderId").value(5));
    }

    @Test
    void downloadDocument() throws Exception {
        ByteArrayResource resource =
                new ByteArrayResource("pdf content".getBytes()) {
                    @Override
                    public String getFilename() {
                        return "manual.pdf";
                    }
                };

        when(documentService.getDocumentResource(10L))
                .thenReturn(resource);

        mockMvc.perform(
                get("/api/documents/10/download")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(
                        header().string(
                                "Content-Disposition",
                                "inline; filename=\"manual.pdf\""
                        )
                )
                .andExpect(content().bytes(
                        "pdf content".getBytes()
                ));
    }

    @Test
    void deleteDocument() throws Exception {
        doNothing()
                .when(documentService)
                .deleteDocument(10L);

        mockMvc.perform(
                delete("/api/documents/10")
        )
                .andExpect(status().isNoContent());

        verify(documentService)
                .deleteDocument(10L);
    }

    private DocumentResponse createResponse() {
        DocumentResponse response = new DocumentResponse();

        response.setId(10L);
        response.setFileName("manual.pdf");
        response.setContentType("application/pdf");
        response.setFileSize(100L);
        response.setDocumentType("MANUAL");

        return response;
    }
}