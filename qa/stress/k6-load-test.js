import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 10 },
    { duration: '60s', target: 30 },
    { duration: '30s', target: 0 }
  ],
  thresholds: {
    http_req_duration: ['p(95)<1200'],
    http_req_failed: ['rate<0.05']
  }
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';

function randomFrom(items) {
  return items[Math.floor(Math.random() * items.length)];
}

export default function () {
  const ticketResp = http.get(`${BASE}/api/tickets`);
  check(ticketResp, {
    'tickets list status 200': (r) => r.status === 200
  });

  const kbResp = http.get(`${BASE}/api/kb/search?query=password`);
  check(kbResp, {
    'kb search status 200': (r) => r.status === 200
  });

  const payload = JSON.stringify({
    customerId: randomFrom(['C001', 'C002', 'C003']),
    channel: randomFrom(['Voice', 'Email', 'Chat']),
    severity: randomFrom(['Low', 'High', 'Critical']),
    description: `Load test issue ${Date.now()}`,
    agentId: 'A101',
    referenceId: `REF-${Math.floor(Math.random() * 10000)}`
  });

  const createResp = http.post(`${BASE}/api/tickets`, payload, {
    headers: { 'Content-Type': 'application/json' }
  });

  check(createResp, {
    'create ticket status 200': (r) => r.status === 200,
    'create ticket has id': (r) => {
      try {
        return !!JSON.parse(r.body).ticketId;
      } catch (e) {
        return false;
      }
    }
  });

  sleep(1);
}
