package com.nttdata.microservices.report.util;

import java.text.MessageFormat;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtils {
  private static MessageSource messageSource;

  public MessageUtils(MessageSource messageSource) {
    MessageUtils.messageSource = messageSource;
  }

  public static String getMsg(String key, Object... value) {
    try {
      String message = messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
      MessageFormat messageFormat = new MessageFormat(message);
      return messageFormat.format(value);
    } catch (Exception e) {
      return key;
    }
  }

  public static String getMsg(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

}
