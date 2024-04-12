package com.github.compscidr.awm_server

import io.quarkus.qute.Location
import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/")
class HomeResource {
    @Location("home") lateinit var homeTemplate: Template

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/")
    fun home(): TemplateInstance {
        return homeTemplate.data("title", "AWM - Android Wireless Measurement Library")
    }
}