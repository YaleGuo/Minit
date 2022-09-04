package com.minit;

public interface Loader {
	public Container getContainer();
	public void setContainer(Container container);
	public String getPath();
	public void setPath(String path);
	public String getDocbase();
	public void setDocbase(String docbase);
	  public ClassLoader getClassLoader();
	  public String getInfo();
	  public void addRepository(String repository);
	  public String[] findRepositories();
	  public void start();
	  public void stop();
}
