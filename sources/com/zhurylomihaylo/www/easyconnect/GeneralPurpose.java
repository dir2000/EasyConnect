package com.zhurylomihaylo.www.easyconnect;

import java.net.InetAddress;
import java.net.UnknownHostException;

class GeneralPurpose {
	static Pair<String> getIP(String comp, boolean suffixNeeded) {
		String firstTryComp, secondTryComp;
		Pair<String> pair = new Pair<>();
		pair.setFirst(comp);
		
		if (suffixNeeded) {
			firstTryComp = comp + ".megapolis.local";
			secondTryComp = comp;
		} else {
			firstTryComp = comp;
			secondTryComp = comp + ".megapolis.local";					
		}
		try {
			InetAddress address = InetAddress.getByName(firstTryComp);
			pair.setFirst(firstTryComp);
			pair.setSecond(address.getHostAddress());
		} catch (UnknownHostException e1) {
			try {
				InetAddress address = InetAddress.getByName(secondTryComp);
				pair.setFirst(secondTryComp);
				pair.setSecond(address.getHostAddress());
			} catch (UnknownHostException e) {
			}
		}	
		
		return pair;
	}

	static String getComp(String ip) {
		String comp = null;
		try {
			InetAddress address = InetAddress.getByName(ip);
			comp = address.getHostName();
			if (comp.equals(ip))
				comp = null;
		} catch (UnknownHostException e) {
		}
		
		return comp;
	}
}
