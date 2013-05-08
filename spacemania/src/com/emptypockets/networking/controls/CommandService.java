package com.emptypockets.networking.controls;

import com.emptypockets.networking.client.ClientManager;
import com.emptypockets.networking.controls.commands.client.ClientConnectCommand;
import com.emptypockets.networking.controls.commands.server.ServerStartCommand;
import com.emptypockets.networking.controls.commands.server.ServerStatusCommand;
import com.emptypockets.networking.controls.commands.server.ServerStopCommand;
import com.emptypockets.networking.server.ServerManager;

public class CommandService {

	public static void registerServer(ServerManager server) {
		server.getCommand().addCommand(new ServerStartCommand(server));
		server.getCommand().addCommand(new ServerStopCommand(server));
		server.getCommand().addCommand(new ServerStatusCommand(server));
	}

	public static void registerClient(ClientManager client) {
		client.getCommand().addCommand(new ClientConnectCommand(client));
		
		client.getCommand().getPanel().pushHistory("\\connect 54.217.240.178,9080,9081");
		client.getCommand().getPanel().pushHistory("\\connect localhost,9080,9081");
	}
}