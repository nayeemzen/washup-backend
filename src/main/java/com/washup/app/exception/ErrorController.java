package com.washup.app.exception;

import com.washup.protos.Shared;
import com.washup.protos.Shared.Error;
import com.washup.protos.Shared.Error.Builder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

@Controller
public class ErrorController {
  @Autowired
  private ErrorAttributes errorAttributes;

  @RequestMapping(value = "/error", produces = "application/x-protobuf")
  @ResponseBody
  public ResponseEntity<Shared.Error> error(HttpServletRequest request) {
    Map<String, Object> body = getErrorAttributes(request, getTraceParameter(request));

    Builder builder = Error.newBuilder();
    for(Map.Entry<String, Object> error : body.entrySet()) {
      builder.putFields(error.getKey(), String.valueOf(error.getValue()));
    }

    HttpStatus status = getStatus(request);
    return new ResponseEntity<>(builder.build(), status);
  }

  private boolean getTraceParameter(HttpServletRequest request) {
    String parameter = request.getParameter("trace");
    return parameter != null && !"false".equals(parameter.toLowerCase());
  }

  private Map<String, Object> getErrorAttributes(HttpServletRequest request,
      boolean includeStackTrace) {
    RequestAttributes requestAttributes = new ServletRequestAttributes(request);
    return this.errorAttributes.getErrorAttributes(requestAttributes,
        includeStackTrace);
  }

  private HttpStatus getStatus(HttpServletRequest request) {
    Integer statusCode = (Integer) request
        .getAttribute("javax.servlet.error.status_code");
    if (statusCode != null) {
      try {
        return HttpStatus.valueOf(statusCode);
      }
      catch (Exception ex) {
      }
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}