package com.euromoby.agent.http;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.euromoby.agent.utils.Strings;

@Component
public class HttpClientProvider implements InitializingBean, DisposableBean {

	private CloseableHttpClient httpClient;
	
	private SSLContextProvider sslContextProvider;
	private String httpUserAgent;
	private String httpProxyHost;
	private int httpProxyPort;
	private int httpClientTimeout;

	public SSLContextProvider getSslContextProvider() {
		return sslContextProvider;
	}

	public void setSslContextProvider(SSLContextProvider sslContextProvider) {
		this.sslContextProvider = sslContextProvider;
	}

	public String getHttpUserAgent() {
		return httpUserAgent;
	}

	public void setHttpUserAgent(String httpUserAgent) {
		this.httpUserAgent = httpUserAgent;
	}

	public String getHttpProxyHost() {
		return httpProxyHost;
	}

	public void setHttpProxyHost(String httpProxyHost) {
		this.httpProxyHost = httpProxyHost;
	}

	public int getHttpProxyPort() {
		return httpProxyPort;
	}

	public void setHttpProxyPort(int httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}

	public int getHttpClientTimeout() {
		return httpClientTimeout;
	}

	public void setHttpClientTimeout(int httpClientTimeout) {
		this.httpClientTimeout = httpClientTimeout;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.httpClient = createHttpClient();
	}

	protected CloseableHttpClient createHttpClient() {
		return HttpClientBuilder.create().setSslcontext(sslContextProvider.getSSLContext()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
				.setUserAgent(httpUserAgent).build();
	}

	public RequestConfig.Builder createRequestConfigBuilder() {

		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		requestConfigBuilder.setSocketTimeout(httpClientTimeout);
		requestConfigBuilder.setConnectTimeout(httpClientTimeout);

		if (!Strings.nullOrEmpty(httpProxyHost)) {
			requestConfigBuilder.setProxy(new HttpHost(httpProxyHost, httpProxyPort));
		}
		return requestConfigBuilder;
	}

	public CloseableHttpResponse executeRequest(HttpUriRequest httpRequest) throws ClientProtocolException, IOException {
		return httpClient.execute(httpRequest);
	}

	@Override
	public void destroy() throws Exception {
		IOUtils.closeQuietly(httpClient);
	}

}
