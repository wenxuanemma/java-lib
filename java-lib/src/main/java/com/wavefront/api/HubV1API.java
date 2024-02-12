package com.wavefront.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.wavefront.api.agent.AgentConfiguration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * v2 API for the proxy.
 *
 * @author vasily@wavefront.com
 */
@Path("/")
public interface HubV1API {

  /**
   * Register the proxy and transmit proxy metrics to Wavefront servers.
   *
   * @param proxyId       ID of the proxy.
   * @param authorization Authorization token.
   * @param hostname      Host name of the proxy.
   * @param proxyname     Proxy name of the proxy (used as internal metric source).
   * @param version       Build version of the proxy.
   * @param currentMillis Current time at the proxy (used to calculate clock drift).
   * @param agentMetrics  Proxy metrics.
   * @param ephemeral     If true, proxy is removed from the UI after 24 hours of inactivity.
   * @return Proxy configuration.
   */
  @POST
  @Path("v1/hub/checkin")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  AgentConfiguration proxyCheckin(@HeaderParam("X-WF-PROXY-ID") final UUID proxyId,
                                  @HeaderParam("Authorization") String authorization,
                                  @QueryParam("hostname") String hostname,
                                  @QueryParam("proxyname") String proxyname,
                                  @QueryParam("version") String version,
                                  @QueryParam("currentMillis") final Long currentMillis,
                                  JsonNode agentMetrics,
                                  @QueryParam("ephemeral") Boolean ephemeral);
                                  
  @POST
  @Path("v1/hub/saveConfig")
  @Consumes(MediaType.APPLICATION_JSON)
  void proxySaveConfig(@HeaderParam("X-WF-PROXY-ID") final UUID proxyId,
                                  JsonNode proxyConfig);
  
  @POST
  @Path("v1/hub/savePreprocessorRules")
  @Consumes(MediaType.APPLICATION_JSON)
  void proxySavePreprocessorRules(@HeaderParam("X-WF-PROXY-ID") final UUID proxyId,
                       JsonNode proxyPreprocessorRules);

  /**
   * Report batched data (metrics, histograms, spans, etc) to Wavefront servers.
   *
   * @param proxyId       Proxy Id reporting the result.
   * @param format        The format of the data (wavefront, histogram, trace, spanLogs)
   * @param pushData      Push data batch (newline-delimited)
   */
  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  @Path("v1/hub/report")
  Response proxyReport(@HeaderParam("X-WF-PROXY-ID") final UUID proxyId,
                       @QueryParam("format") final String format,
                       final String pushData);

  /**
   * Reports confirmation that the proxy has processed and accepted the configuration sent from the back-end.
   *
   * @param proxyId ID of the proxy.
   */
  @POST
  @Path("v1/hub/config/processed")
  void proxyConfigProcessed(@HeaderParam("X-WF-PROXY-ID") final UUID proxyId);

  /**
   * Reports an error that occurred in the proxy.
   *
   * @param proxyId ID of the proxy reporting the error.
   * @param details Details of the error.
   */
  @POST
  @Path("v1/hub/error")
  void proxyError(@HeaderParam("X-WF-PROXY-ID") final UUID proxyId,
                  @FormParam("details") String details);
}
