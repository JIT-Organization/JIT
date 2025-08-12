import { useRef, useEffect, useCallback, useState } from "react";
import { Client as StompClient, CompatClient } from "@stomp/stompjs";

/**
 * useWebSocket — React hook that manages a STOMP-over-WebSocket connection.
 *
 * @param {string} url full WS endpoint, e.g. "ws://localhost:8080/ws?user=<token>"
 */
const useWebSocket = (url) => {
  const stompRef = useRef(/** @type {CompatClient|null} */ (null));
  const subsRef = useRef(/** @type {Record<string, any>} */ ({}));
  const pendingSubsRef = useRef(/** @type {Array<{dest:string,fn:Function}>} */ ([]));
  const [isConnected, setIsConnected] = useState(false);

  /* establish / tear down the connection */
  useEffect(() => {
    const client = new StompClient({
      brokerURL: url,
      reconnectDelay: 60_0000,
      debug: (msg) => {console.log(msg)},
      connectHeaders: {
        'Accept': 'application/json',
      },
      onConnect: () => {
        setIsConnected(true);
        // flush queued subscriptions
        pendingSubsRef.current.forEach(({ dest, fn }) => doSubscribe(dest, fn));
        pendingSubsRef.current = [];
      },
      onDisconnect: () => setIsConnected(false),
      onStompError: (frame) => {
        // eslint-disable-next-line no-console
        console.error("STOMP error", frame.headers.message, frame.body);
      },
    });

    client.activate();
    stompRef.current = client;

    return () => {
      client.deactivate();     // graceful shutdown
      subsRef.current = {};
      setIsConnected(false);
    };
  }, [url]);

  /* internal helper — avoids re-creating closure */
  const doSubscribe = (dest, fn) => {
    if (!stompRef.current) return;
    const sub = stompRef.current.subscribe(dest, (m) => fn(JSON.parse(m.body)));
    subsRef.current[dest] = sub;
  };

  /* public subscribe() */
  const subscribe = useCallback((destination, onMessage) => {
    if (!isConnected) {
      pendingSubsRef.current.push({ dest: destination, fn: onMessage });
      return () => {};
    }
    if (subsRef.current[destination]) return () => {};      // already sub’d

    doSubscribe(destination, onMessage);

    return () => {
      subsRef.current[destination]?.unsubscribe();
      delete subsRef.current[destination];
    };
  }, [isConnected]);

  /* public sendMessage() */
  const sendMessage = useCallback((destination, body) => {
    if (isConnected && stompRef.current) {
      stompRef.current.publish({
        destination,
        body: JSON.stringify(body),
      });
    } else {
      console.warn("WebSocket not connected; message dropped");
    }
  }, [isConnected]);

  return { subscribe, sendMessage, isConnected };
};

export default useWebSocket;
