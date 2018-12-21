package io.github.teambanhammer;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TeleportRequest { // A teleport request object.

    private static List<TeleportRequest> requestList = new ArrayList<>();
    private Player requester; // The player sending the request.
    private Player responder; // The player receiving it.
    private BukkitRunnable timer;
    private TeleportType type;

    public TeleportRequest(Player requester, Player responder, BukkitRunnable timer, TeleportType type) {
        this.requester = requester;
        this.responder = responder;
        this.timer = timer;
        this.type = type;
    }

    public BukkitRunnable getTimer() {
        return timer;
    }

    public Player getRequester() {
        return requester;
    }

    public Player getResponder() {
        return responder;
    }

    public TeleportType getType() {
        return type;
    }

    public enum TeleportType {
        TPA_HERE,
        TPA_NORMAL
    }

    public static List<TeleportRequest> getRequests(Player responder) {
        List<TeleportRequest> requests = new ArrayList<>();
        for (TeleportRequest request : requestList) {
            if (request.responder == responder) {
                requests.add(request);
            }
        }
        return requests;
    }

    public static List<TeleportRequest> getRequestsByRequester(Player requester) {
        List<TeleportRequest> requests = new ArrayList<>(); // Requests that the requester has pending
        for (TeleportRequest request : requestList) {
            if (request.getRequester() == requester) {
                requests.add(request);
            }
        }
        return requests;
    }

    public static TeleportRequest getRequestByReqAndResponder(Player responder, Player requester) {
        for (TeleportRequest request : requestList) {
            if (request.getRequester() == requester && request.getResponder() == responder) {
                return request;
            }
        }
        return null;
    }

    public static void addRequest(TeleportRequest request) {
        requestList.add(request);
    }

    public static void removeRequest(TeleportRequest request) {
        requestList.remove(request);
    }

    public void destroy() {
        timer.cancel();
        removeRequest(this);
    }
}
