export const NOTIFICATION_TYPES = {
  ORDER_CREATED: 'ORDER_CREATED',
  ORDER_UPDATED: 'ORDER_UPDATED',
  ORDER_READY: 'ORDER_READY',
  ORDER_SERVED: 'ORDER_SERVED',
  KITCHEN_STATUS: 'KITCHEN_STATUS',
  TABLE_STATUS: 'TABLE_STATUS',
  MENU_UPDATE: 'MENU_UPDATE',
  USER_UPDATE: 'USER_UPDATE',
  SYSTEM_ALERT: 'SYSTEM_ALERT',
  ROLE_UPDATE: 'ROLE_UPDATE',
  PAYMENT_STATUS: 'PAYMENT_STATUS',
  ANNOUNCEMENT: 'ANNOUNCEMENT',
  TABLE_ASSIGNED: 'TABLE_ASSIGNED',
  SHIFT_UPDATE: 'SHIFT_UPDATE'
};

export const NOTIFICATION_PRIORITIES = {
  LOW: 'low',
  MEDIUM: 'medium',
  HIGH: 'high',
  URGENT: 'urgent'
};

export const getNotificationConfig = (type) => {
  const configs = {
    [NOTIFICATION_TYPES.ORDER_CREATED]: {
      title: 'New Order Created',
      icon: '🛍️',
      color: 'blue',
      priority: NOTIFICATION_PRIORITIES.HIGH
    },
    [NOTIFICATION_TYPES.ORDER_UPDATED]: {
      title: 'Order Updated',
      icon: '📝',
      color: 'yellow',
      priority: NOTIFICATION_PRIORITIES.MEDIUM
    },
    [NOTIFICATION_TYPES.ORDER_READY]: {
      title: 'Order Ready',
      icon: '✅',
      color: 'green',
      priority: NOTIFICATION_PRIORITIES.HIGH
    },
    [NOTIFICATION_TYPES.KITCHEN_STATUS]: {
      title: 'Kitchen Update',
      icon: '👨‍🍳',
      color: 'orange',
      priority: NOTIFICATION_PRIORITIES.MEDIUM
    },
    [NOTIFICATION_TYPES.TABLE_STATUS]: {
      title: 'Table Status',
      icon: '🪑',
      color: 'purple',
      priority: NOTIFICATION_PRIORITIES.MEDIUM
    },
    [NOTIFICATION_TYPES.MENU_UPDATE]: {
      title: 'Menu Update',
      icon: '📋',
      color: 'green',
      priority: NOTIFICATION_PRIORITIES.MEDIUM
    },
    [NOTIFICATION_TYPES.ROLE_UPDATE]: {
      title: 'Role Updated',
      icon: '👤',
      color: 'indigo',
      priority: NOTIFICATION_PRIORITIES.MEDIUM
    },
    [NOTIFICATION_TYPES.SYSTEM_ALERT]: {
      title: 'System Alert',
      icon: '⚠️',
      color: 'red',
      priority: NOTIFICATION_PRIORITIES.HIGH
    },
    [NOTIFICATION_TYPES.PAYMENT_STATUS]: {
      title: 'Payment Status',
      icon: '💳',
      color: 'green',
      priority: NOTIFICATION_PRIORITIES.HIGH
    },
    [NOTIFICATION_TYPES.ANNOUNCEMENT]: {
      title: 'Announcement',
      icon: '📢',
      color: 'blue',
      priority: NOTIFICATION_PRIORITIES.MEDIUM
    },
    [NOTIFICATION_TYPES.TABLE_ASSIGNED]: {
      title: 'Table Assignment',
      icon: '🪑',
      color: 'purple',
      priority: NOTIFICATION_PRIORITIES.HIGH
    },
    [NOTIFICATION_TYPES.SHIFT_UPDATE]: {
      title: 'Shift Update',
      icon: '⏰',
      color: 'orange',
      priority: NOTIFICATION_PRIORITIES.MEDIUM
    }
  };
  
  return configs[type] || {
    title: 'Notification',
    icon: '🔔',
    color: 'gray',
    priority: NOTIFICATION_PRIORITIES.LOW
  };
};
