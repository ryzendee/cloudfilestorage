'use strict'

const TIME = 3000;
const ALERT_CLASS = ".alert";

document.addEventListener('DOMContentLoaded', () => {
    closeAlertsAfterTime(TIME);
});

function closeAlertsAfterTime(time) {
    const alerts = document.querySelectorAll(ALERT_CLASS);
    alerts.forEach(alert => {
        const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);

        setTimeout(() => {
            bsAlert.close();
            }, time);
    })
}

