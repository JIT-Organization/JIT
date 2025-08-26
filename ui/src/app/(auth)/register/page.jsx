import React, { Suspense } from "react";
import LoginAndResgisterForm from "../../../components/LoginAndRegisterForm";

const page = () => {
  return (
    <div>
      <Suspense fallback={<div>Loading...</div>}>
        <LoginAndResgisterForm />
      </Suspense>
    </div>
  );
};

export default page;
