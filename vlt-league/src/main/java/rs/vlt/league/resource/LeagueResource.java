package rs.vlt.league.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import rs.vlt.league.service.LeagueService;
import rs.vlt.league.entity.Season;
import rs.vlt.league.dto.ApiDtos.SeasonSummary;
import rs.vlt.league.dto.ApiDtos.OrganisationSummary;
import rs.vlt.league.dto.ApiDtos.OrganisationRequest;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeagueResource {
    @Inject LeagueService service;
    @GET @Path("/dashboard") public Object dashboard() { return service.dashboard(); }
    @GET @Path("/runners") public Object runners(@DefaultValue("0") @QueryParam("page") int page, @DefaultValue("20") @QueryParam("size") int size, @QueryParam("q") String query, @QueryParam("sort") String sort) { return service.runnerPage(page, Math.min(Math.max(size, 1), 100), query, sort); }
    @GET @Path("/runners/{id}") public Object runner(@PathParam("id") Integer id) { return service.runner(id); }
    @GET @Path("/seasons") public List<SeasonSummary> seasons() { return service.seasonList().stream().map(s -> new SeasonSummary(s.id, s.name)).toList(); }
    @GET @Path("/organisations") public List<OrganisationSummary> organisations() { return service.organisationList(); }
    @POST @Path("/organisations") public OrganisationSummary createOrganisation(OrganisationRequest request) { return service.createOrganisation(request); }
    @PUT @Path("/organisations/{id}") public OrganisationSummary updateOrganisation(@PathParam("id") Integer id, OrganisationRequest request) { return service.updateOrganisation(id, request); }
    @DELETE @Path("/organisations/{id}") public void deleteOrganisation(@PathParam("id") Integer id) { service.deleteOrganisation(id); }
    @GET @Path("/races") public Object races(@DefaultValue("0") @QueryParam("page") int page, @DefaultValue("20") @QueryParam("size") int size, @QueryParam("seasonId") Integer seasonId) { return service.racePage(page, Math.min(Math.max(size, 1), 1000), seasonId); }
    @GET @Path("/races/{id}") public Object race(@PathParam("id") Integer id) { return service.race(id); }
    @GET @Path("/races/{id}/results") public Object results(@PathParam("id") Integer id) { return service.results(id); }
    @GET @Path("/standings") public Object standings(@QueryParam("seasonId") Integer seasonId, @QueryParam("length") Integer length, @QueryParam("gender") String gender) { if (seasonId == null) throw new BadRequestException("seasonId is required"); return service.standings(seasonId, length, gender); }
}