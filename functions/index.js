const functions = require("firebase-functions");
const nodemailer = require("nodemailer");
const admin = require("firebase-admin");

admin.initializeApp();

// Configure email transport using Gmail
const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: "souschef.samsantech@gmail.com",
    pass: "Pass12**12345678",
  },
});

// Send OTP email function
exports.sendOtpEmail = functions.https.onCall(async (data, context) => {
  const email = data.email;
  const otp = Math.floor(100000 + Math.random() * 900000);

  console.log(`Sending OTP ${otp} to ${email}`);

  // Send OTP to the provided email address
  const mailOptions = {
    from: "souschef.samsantech@gmail.com",
    to: email,
    subject: "Your OTP Code",
    text: `Your OTP code is: ${otp}`,
  };

  try {
    await transporter.sendMail(mailOptions);
    // Save the OTP to Firestore or another secure storage for validation
    await admin.firestore().collection("otpRequests").add({
      email: email,
      otp: otp,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
    });

    return {success: true, message: "OTP sent to email."};
  } catch (error) {
    return {success: false, message: error.message};
  }
});

// Verify OTP function
exports.verifyOtp = functions.https.onCall(async (data, context) => {
  const email = data.email;
  const otpEntered = data.otp;

  // Check OTP from Firestore
  const otpDoc = await admin.firestore().collection("otpRequests")
      .where("email", "==", email)
      .orderBy("timestamp", "desc")
      .limit(1)
      .get();

  if (otpDoc.empty) {
    return {success: false, message: "OTP not found or expired."};
  }

  const otpData = otpDoc.docs[0].data();
  const otpStored = otpData.otp;

  if (otpEntered === otpStored) {
    return {success: true, message: "OTP verified successfully."};
  } else {
    return {success: false, message: "Incorrect OTP."};
  }
});
