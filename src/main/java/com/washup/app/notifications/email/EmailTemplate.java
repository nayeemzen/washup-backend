package com.washup.app.notifications.email;

public enum EmailTemplate {
  WELCOME("003d14f5-d12c-46a9-ba0f-bcb562d9307a", "Welcome to WashUp!");

  private final String templateId;
  private final String subject;

  EmailTemplate(String templateId, String subject) {
    this.templateId = templateId;
    this.subject = subject;
  }

  public String templateId() {
    return templateId;
  }

  public String subject() {
    return subject;
  }
}
