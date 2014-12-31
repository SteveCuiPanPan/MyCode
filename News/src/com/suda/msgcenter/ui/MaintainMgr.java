package com.suda.msgcenter.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MaintainMgr {

	private static MaintainMgr mgr;

	private ExecutorService executorService = null;
	private MaintainMgr() {
		executorService = Executors.newFixedThreadPool(6);
	}

	public static MaintainMgr getInstance() {
		if (mgr == null) {
			mgr = new MaintainMgr();
		}
		return mgr;
	}

	
	public void runBackground(Runnable command) {
		executorService.execute(command);
	}

}
