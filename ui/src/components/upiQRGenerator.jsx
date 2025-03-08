'use client';

import { useEffect, useState } from 'react';
import QRCode from 'react-qr-code';    
import { v4 as uuidv4 } from 'uuid';
import { AlertTriangle } from 'lucide-react';
import { Progress } from './ui/progress';

export default function UpiQrGenerator({isExpanded : isExpanded, setIsExpanded : setIsExpanded}) {
  const [upiId, setUpiId] = useState('9384524556@ibl');
  const [amount, setAmount] = useState('1000');
  const [qr,setQR] = useState('');
  const [expiryTime, setExpiryTime] = useState(null);
  const [timeLeft, setTimeLeft] = useState(0);
  const [isSessionExpired,setIsSessionExpiered] = useState(false)
  const totalDuration = 1 * 10; 

  const generateUpiUrl = () => {
    console.log("adfad",upiId,amount,qr);
    if (!upiId || !amount) return '';
    const transactionId = uuidv4()
    const expirationDuration = totalDuration*1000; // 5 minutes
    const expiryTimestamp = Date.now() + expirationDuration;
    setExpiryTime(expiryTimestamp);
    setTimeLeft(expirationDuration / 1000);
    setIsSessionExpiered(false);

    setQR(`upi://pay?pa=${encodeURIComponent(upiId)}&pn=Receiver&am=${encodeURIComponent(amount)}&cu=INR&tid=${transactionId}`);
    
};

useEffect(()=>{
    generateUpiUrl();
},[])

useEffect(() => {
    if (!expiryTime) return;

    const interval = setInterval(() => {
      const remaining = Math.max(0, Math.floor((expiryTime - Date.now()) / 1000));
      setTimeLeft(remaining);
      if (remaining === 0) {
        setQR(null);
        setIsSessionExpiered(true)
        const timeout = setTimeout(() => {
            setIsExpanded(false); // Update the delayed state after a short delay
          }, 3000);
        
        clearInterval(interval);
      }
    }, 1000);

    return () => clearInterval(interval);
  }, [expiryTime]);

  return (
    <div className="flex flex-col items-center gap-4 p-4">
      {/* <input
        type="text"
        placeholder="Enter UPI ID"
        value={upiId}
        onChange={(e) => setUpiId(e.target.value)}
        className="border p-2 rounded w-64"
      /> */}
      {/* <input
        type="number"
        placeholder="Enter Amount"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        className="border p-2 rounded w-64"
      />
      <button  onClick={()=>generateUpiUrl()}>
        Generate QR
      </button> */}
      {qr && (
         <div className="flex flex-col items-center gap-2">
        <QRCode value={qr} size={200} />
        </div>
      )}
        {timeLeft > 0 && (
            <div className="w-64">
              <Progress value={(timeLeft / totalDuration) * 100} className="mb-2" />
              <p className="text-gray-600">Expires in {timeLeft} seconds</p>
            </div>
          )}  
          {isSessionExpired &&(
            <div className="flex items-center text-red-600 gap-2">
              <AlertTriangle className="w-6 h-6" />
              <span>Session Expired</span>
            </div>
          )}
    </div>
  );
}
