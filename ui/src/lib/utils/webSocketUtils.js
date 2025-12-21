import { useRef, useEffect, useCallback, useState } from "react";
import { Client as StompClient } from "@stomp/stompjs";

/**
 * useWebSocket — React hook that manages a STOMP-over-WebSocket connection.
 *
 * @param {string} url full WS endpoint, e.g. "ws://localhost:8080/ws"
 */
const useWebSocket = (url) => {
  const stompRef = useRef(/** @type {CompatClient|null} */ (null));
  const subsRef = useRef(/** @type {Record<string, any>} */ ({}));
  const pendingSubsRef = useRef(/** @type {Array<{dest:string,fn:Function}>} */ ([]));
  const [isConnected, setIsConnected] = useState(false);

  const doSubscribe = useCallback((dest, fn) => {
    if (!stompRef.current) return;
    console.log(`Subscribing to: ${dest}`);
    const sub = stompRef.current.subscribe(dest, (m) => fn(JSON.parse(m.body)));
    subsRef.current[dest] = sub;
  }, []);

  useEffect(() => {
    if (!url) return;

    const client = new StompClient({
      brokerURL: url,
      reconnectDelay: 5000,
      debug: (msg) => { console.log(msg) },
      connectHeaders: {
        'Accept': 'application/json',
      },
      onConnect: () => {
        setIsConnected(true);
        pendingSubsRef.current.forEach(({ dest, fn }) => doSubscribe(dest, fn));
        pendingSubsRef.current = [];
      },
      onDisconnect: () => setIsConnected(false),
      onStompError: (frame) => {
        console.error("STOMP error", frame.headers.message, frame.body);
      },
      onWebSocketError: (event) => {
        console.error("WebSocket connection error:", event);
      }
    });
    
    stompRef.current = client; 
    client.activate();

    return () => {
      client.deactivate();
      subsRef.current = {};
      setIsConnected(false);
    };
  }, [url, doSubscribe]);

  /* public subscribe() */
  const subscribe = useCallback((destination, onMessage) => {
    if (!isConnected) {
      pendingSubsRef.current.push({ dest: destination, fn: onMessage });
      return () => {
        pendingSubsRef.current = pendingSubsRef.current.filter(
          (sub) => sub.dest !== destination
        );
      };
    }
    
    if (subsRef.current[destination]) {
      return () => {
        subsRef.current[destination]?.unsubscribe();
        delete subsRef.current[destination];
      };
    }

    doSubscribe(destination, onMessage);

    return () => {
      subsRef.current[destination]?.unsubscribe();
      delete subsRef.current[destination];
    };
  }, [isConnected, doSubscribe]);

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