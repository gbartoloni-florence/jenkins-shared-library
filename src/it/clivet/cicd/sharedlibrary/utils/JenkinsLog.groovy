package it.clivet.cicd.sharedlibrary.utils

import java.util.logging.Logger

class JenkinsLog implements DeployerLog {

                        

  private Logger log = Logger.getLogger('org.example.jobdsl')

  public JenkinsLog() {
    this.log = log;
  }

  @Override
  public void info(String charSequence) {
    log.info(charSequence);
  }

  @Override
  public void error(String charSequence) {
    log.error(charSequence);
  }

  @Override
  public void warn(String charSequence) {
    log.warn(charSequence);
  }

  @Override
  public void debug(String charSequence) {
    log.debug(charSequence);
  }

  @Override
  public void error(String charSequence, Throwable e) {
    log.error(charSequence, e);
  }

  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }
}