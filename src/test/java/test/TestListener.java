package test;

import com.minit.ContainerEvent;
import com.minit.ContainerListener;

public class TestListener implements ContainerListener{

	@Override
	public void containerEvent(ContainerEvent event) {
		// TODO Auto-generated method stub
		System.out.println("Host Listener ...");
		System.out.println(event);
	}

}
