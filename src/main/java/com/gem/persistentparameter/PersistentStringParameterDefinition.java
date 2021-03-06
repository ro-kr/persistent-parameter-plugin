/*
 * The MIT License
 * 
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Luca Domenico Milanesio, Seiji Sogabe, Tom Huybrechts
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.gem.persistentparameter;

import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.ParameterDefinition;
import hudson.model.StringParameterValue;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Parameter whose value is a string value.
 */
public class PersistentStringParameterDefinition extends SimpleParameterDefinition
{
  private static final long serialVersionUID = -7077702646937544392L;
  private String defaultValue;
  private final boolean successfulOnly;

  @DataBoundConstructor
  public PersistentStringParameterDefinition(String name, String defaultValue, boolean successfulOnly, String description)
  {
    super(name, description);
    this.defaultValue = defaultValue;
    this.successfulOnly = successfulOnly;
  }

  @Override
  public ParameterDefinition copyWithDefaultValue(ParameterValue defaultValue)
  {
    if(defaultValue instanceof StringParameterValue)
    {
      StringParameterValue value = (StringParameterValue)defaultValue;
      return new PersistentStringParameterDefinition(getName(), value.value, isSuccessfulOnly(), getDescription());
    }
    else
    {
      return this;
    }
  }

  public String getDefaultValue()
  {
    try
    {
      ParameterValue lastValue = CurrentProject.getLastValue(this, successfulOnly);
      return ((StringParameterValue)lastValue).value;
    }
    catch(Exception ex)
    {
    }
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  public boolean isSuccessfulOnly()
  {
    return successfulOnly;
  }

  @Override
  public StringParameterValue getDefaultParameterValue()
  {
    StringParameterValue v = new StringParameterValue(getName(), getDefaultValue(), getDescription());
    return v;
  }

  @Override
  public ParameterValue createValue(StaplerRequest req, JSONObject jo)
  {
    StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
    value.setDescription(getDescription());
    return value;
  }

  @Override
  public ParameterValue createValue(String value)
  {
    return new StringParameterValue(getName(), value, getDescription());
  }

  @Extension
  public static class DescriptorImpl extends ParameterDescriptor
  {
    @Override
    public String getDisplayName()
    {
      return "Persistent String Parameter";
    }

    @Override
    public String getHelpFile()
    {
      return "/help/parameter/string.html";
    }
  }
}
