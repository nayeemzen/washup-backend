package com.washup.app.spring;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.X_PROTOBUF_MESSAGE_HEADER;
import static org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter.X_PROTOBUF_SCHEMA_HEADER;

import com.google.common.base.Charsets;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class ProtobufHttpMessageConverter extends AbstractHttpMessageConverter<Message> {
  public static final MediaType PROTOBUF = new MediaType("application", "x-protobuf", Charsets.UTF_8);
  private final Printer jsonPrinter;

  public ProtobufHttpMessageConverter(Printer jsonPrinter) {
    super(PROTOBUF, MediaType.APPLICATION_JSON);
    this.jsonPrinter = jsonPrinter;
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return Message.class.isAssignableFrom(clazz);
  }

  @Override
  protected Message readInternal(Class<? extends Message> clazz, HttpInputMessage inputMessage)
      throws IOException, HttpMessageNotReadableException {
    MediaType contentType = inputMessage.getHeaders().getContentType();
    if (contentType == null) {
      contentType = PROTOBUF;
    }

    try {
      Message.Builder builder = (Message.Builder) clazz.getMethod("newBuilder").invoke(clazz);
      if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputMessage.getBody(), Charsets.UTF_8));
        JsonFormat.parser().merge(bufferedReader, builder);
        return builder.build();
      }

      if (PROTOBUF.isCompatibleWith(contentType)) {
        return builder.mergeFrom(inputMessage.getBody()).build();
      }

      throw new UnsupportedOperationException(contentType.toString() + " is not supported.");
    } catch (Throwable t) {
      throw new HttpMessageNotReadableException("Could not read protobuf message: " + t.getMessage());
    }
  }

  @Override
  protected void writeInternal(Message message, HttpOutputMessage outputMessage)
      throws IOException, HttpMessageNotWritableException {
    MediaType contentType = outputMessage.getHeaders().getContentType();
    if (contentType == null) {
      contentType = PROTOBUF;
    }

    try {
      if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputMessage.getBody(),
            Charsets.UTF_8);
        outputStreamWriter.write(jsonPrinter.print(message));
        outputStreamWriter.flush();
      } else if (contentType.isCompatibleWith(PROTOBUF)) {
        setProtoHeader(outputMessage, message);
        outputMessage.getBody().write(message.toByteArray());
      } else {
        throw new UnsupportedOperationException(contentType + " is not supported.");
      }
    } catch (Throwable t) {
      throw new HttpMessageNotWritableException("Could not write protobuf message: " + t.getMessage());
    }
  }

  private void setProtoHeader(HttpOutputMessage response, Message message) {
    response.getHeaders().set(X_PROTOBUF_SCHEMA_HEADER, message.getDescriptorForType().getFile().getName());
    response.getHeaders().set(X_PROTOBUF_MESSAGE_HEADER, message.getDescriptorForType().getFullName());
  }
}
