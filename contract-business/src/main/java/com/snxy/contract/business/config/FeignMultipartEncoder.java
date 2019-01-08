package com.snxy.contract.business.config;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.form.spring.SpringMultipartEncodedDataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhuchangbin
 * @date 2017/11/5
 */
@Slf4j
public class FeignMultipartEncoder extends FormEncoder {

    private Encoder delegate;
    private final List<HttpMessageConverter<?>> converters ;
    private final HttpHeaders multipartHeaders = new HttpHeaders();
    private final HttpHeaders jsonHeaders = new HttpHeaders();

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public FeignMultipartEncoder() {
        converters = new RestTemplate().getMessageConverters();
        multipartHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (!bodyType.equals(MultipartFile.class)) {
            encodeRequest(object, jsonHeaders, template);
            return;
        }

        MultipartFile file = (MultipartFile) object;
        Map<String, Object> data = Collections.singletonMap(file.getName(), object);
        new SpringMultipartEncodedDataProcessor().process(data, template);
    }

    private void encodeRequest(Object value, HttpHeaders requestHeaders, RequestTemplate template) throws EncodeException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpOutputMessage dummyRequest = new FeignMultipartEncoder.HttpOutputMessageImpl(outputStream, requestHeaders);
        try {
            Class<?> requestType = value.getClass();
            MediaType requestContentType = requestHeaders.getContentType();
            for (HttpMessageConverter<?> messageConverter : converters) {
                if (messageConverter.canWrite(requestType, requestContentType)) {
                    ((HttpMessageConverter<Object>) messageConverter).write(
                            value, requestContentType, dummyRequest);
                    break;
                }
            }
        } catch (IOException ex) {
            throw new EncodeException("Cannot encode request.", ex);
        }
        HttpHeaders headers = dummyRequest.getHeaders();
        if (headers != null) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                template.header(entry.getKey(), entry.getValue());
            }
        }
        /*
        we should use a template output stream... this will cause issues if updateFiles are too big,
        since the whole request will be in memory.
         */
        template.body(outputStream.toByteArray(), UTF_8);
    }

    static boolean isFormRequest(Type type) {
        return MAP_STRING_WILDCARD.equals(type);
    }


    private static class DefaultEncoder implements Encoder {
        @Override
        public void encode(Object o, Type type, RequestTemplate requestTemplate) throws EncodeException {

        }
    }

    private class HttpOutputMessageImpl implements HttpOutputMessage {

        private final OutputStream body;
        private final HttpHeaders headers;

        public HttpOutputMessageImpl(OutputStream body, HttpHeaders headers) {
            this.body = body;
            this.headers = headers;
        }

        @Override
        public OutputStream getBody() throws IOException {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

    }
}
