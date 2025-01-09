const functions = require("firebase-functions");
const admin = require("firebase-admin");
const sgMail = require("@sendgrid/mail");

admin.initializeApp();

// Set your SendGrid API Key
sgMail.setApiKey("lLXIV3N5SJSSutIIafXDRw");

// Send OTP email function
exports.sendOtpEmail = functions.https.onCall(async (data, context) => {
  const email = data.email;
  const otp = Math.floor(100000 + Math.random() * 900000);

  console.log(`Sending OTP ${otp} to ${email}`);

  // Email content
  const msg = {
    to: email,
    from: "nicoampoloquio2003@gmail.com",
    subject: "Your OTP Code",
    text: `Your OTP code is: ${otp}`,
  };

  try {
    // Send OTP email
    await sgMail.send(msg);

    // Save the OTP to Firestore for later validation
    await admin.firestore().collection("otpRequests").add({
      email: email,
      otp: otp,
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
    });

    return {success: true, message: "OTP sent to email."};
  } catch (error) {
    console.error("Error sending email:", error);
    return {success: false, message: error.message};
  }
});

// Verify OTP function
exports.verifyOtp = functions.https.onCall(async (data, context) => {
  const email = data.email;
  const otpEntered = data.otp;

  try {
    // Check OTP from Firestore
    const otpDoc = await admin.firestore()
        .collection("otpRequests")
        .where("email", "==", email)
        .orderBy("timestamp", "desc")
        .limit(1)
        .get();

    if (otpDoc.empty) {
      return {success: false, message: "OTP not found or expired."};
    }

    const otpData = otpDoc.docs[0].data();
    const otpStored = otpData.otp;

    if (parseInt(otpEntered) === otpStored) {
      return {success: true, message: "OTP verified successfully."};
    } else {
      return {success: false, message: "Incorrect OTP."};
    }
  } catch (error) {
    console.error("Error verifying OTP:", error);
    return {success: false, message: error.message};
  }
});
