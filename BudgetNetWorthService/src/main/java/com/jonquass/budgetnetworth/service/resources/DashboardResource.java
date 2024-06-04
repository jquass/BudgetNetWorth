package com.jonquass.budgetnetworth.service.resources;

import com.google.inject.Inject;
import com.hubspot.algebra.Result;
import com.jonquass.budgetnetworth.data.html.HtmlReader;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/dashboard")
@Produces(MediaType.TEXT_HTML)
public class DashboardResource {

    private final HtmlReader htmlReader;

    @Inject
    public DashboardResource(HtmlReader htmlReader) {
        this.htmlReader = htmlReader;
    }

    @GET
    public String getDashboard() {
        Result<String, String> result = htmlReader.readHtml(HtmlReader.HtmlFile.DASHBOARD);
        return result.isOk() ? result.unwrapOrElseThrow() : result.unwrapErrOrElseThrow();
    }

}
