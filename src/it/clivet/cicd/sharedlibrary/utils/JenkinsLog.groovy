package it.clivet.cicd.sharedlibrary.utils

import java.util.logging.Logger
import org.mule.tools.utils.DeployerLog;


class JenkinsLog implements DeployerLog {

                        

  private Logger log = Logger.getLogger('it.clivet.cicd.sharedlibrary.utils.JenkinsLog')

  public JenkinsLog() {
    this.log = log
  }

  @Override
  @NonCPS
  public void info(String charSequence) {
    log.info(charSequence)
  }

  @Override
  @NonCPS
  public void error(String charSequence) {
    log.error(charSequence)
  }

  @Override
  @NonCPS
  public void warn(String charSequence) {
    log.warn(charSequence)
  }

  @Override
  @NonCPS
  public void debug(String charSequence) {
    log.info(charSequence)
  }

  @Override
  @NonCPS
  public void error(String charSequence, Throwable e) {
    log.error(charSequence, e)
  }

  @Override
  @NonCPS
  public boolean isDebugEnabled() {
    return log.isDebugEnabled()
  }
}