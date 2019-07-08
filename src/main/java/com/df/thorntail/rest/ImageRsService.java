package com.df.thorntail.rest;

import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("images")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class ImageRsService {

	@GET
	@Path("{id}")
	public JsonObject findById(@PathParam("id") String id) {
		return Json.createObjectBuilder().add("newid", id).build();
	}
}
