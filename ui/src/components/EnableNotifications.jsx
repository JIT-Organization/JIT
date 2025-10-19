import { subscribeUserToPush } from '../lib/utils/pushNotificationUtils';

const NotificationButton = () => {
  const handleSubscriptionClick = async () => {
    const permission = await Notification.requestPermission();
    if (permission === 'granted') {
      console.log('Notification permission granted.');
      await subscribeUserToPush();
    } else {
      console.warn('Permission for notifications was denied.');
    }
  };

  return (
    <button onClick={handleSubscriptionClick}>
      Enable Notifications
    </button>
  );
};

export default NotificationButton;