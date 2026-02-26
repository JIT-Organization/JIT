import { subscribeForPushNotifications } from "../api/api";

// Helper function to convert the VAPID key
const urlBase64ToUint8Array = (base64String) => {
  const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);
  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
};

// Main function to subscribe the user
export const subscribeUserToPush = async () => {
  if (!('serviceWorker' in navigator) || !('PushManager' in window)) {
    console.warn('Push messaging is not supported');
    return;
  }

  try {
    const swRegistration = await navigator.serviceWorker.register('/sw.js');
    let subscription = await swRegistration.pushManager.getSubscription();

    if (subscription === null) {
      console.log('No subscription found, creating new one.');
      const vapidPublicKey = process.env.NEXT_PUBLIC_VAPID_PUBLIC_KEY;
      if (!vapidPublicKey) {
        console.error('VAPID public key is not defined!');
        return;
      }

      subscription = await swRegistration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: urlBase64ToUint8Array(vapidPublicKey),
      });
    }

    console.log("Subscription Data :: ", subscription)

    subscribeForPushNotifications(subscription);
    console.log('User is subscribed.');
    return true;
  } catch (error) {
    console.error('Failed to subscribe the user: ', error);
    return false;
  }
};