package com.noxapps.familygiftlist.login

import android.app.Activity
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.noxapps.familygiftlist.R
import com.noxapps.familygiftlist.data.AppDatabase
import com.noxapps.familygiftlist.ui.theme.FamilyGiftListTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

@Composable
fun LoginPage(
    auth: FirebaseAuth,
    db: AppDatabase,
    navHostController: NavHostController,
    viewModel: LoginViewModel = LoginViewModel(
        auth,
        db,
        navHostController
    )
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var status = remember {mutableStateOf(true)}     //login(true) / register(false) status
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    )
    val textIconColor = MaterialTheme.colorScheme.onPrimaryContainer

    val scrollState = rememberScrollState()
    var backDoubleTap by remember {mutableStateOf(false)}
    BackHandler(){
        if(backDoubleTap) {
            (context as Activity).finish()
            exitProcess(0)
        }
        else{
            backDoubleTap = true
            Toast.makeText(
                context,
                "Doubletap back to exit",
                Toast.LENGTH_SHORT,
            ).show()
            coroutineScope.launch{
                Thread.sleep(1000)
                MainScope().launch{
                    backDoubleTap = false
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(state = scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.Center),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()


            ) {
                LogoCard()
                if (status.value)
                    LoginCard(textFieldColors, textIconColor, status, viewModel)
                else
                    RegisterCard(textFieldColors, textIconColor, status, viewModel)

            }
        }
    }

}

@Composable
fun LogoCard(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ){
        Image(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally),
            painter = painterResource(id = R.drawable.logo_placeholder),
            contentDescription ="Logo"
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginCard(
    textFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    textIconColors:Color? = null,
    loginState:MutableState<Boolean>,
    viewModel:LoginViewModel
){
    val context = LocalContext.current
    var email by remember {mutableStateOf("")}
    var password by remember{mutableStateOf("")}
    var enabled = remember{mutableStateOf(true)}
    var passwordVisible by remember{mutableStateOf(false)}
    var visibilityDetails =
        if(passwordVisible){
            Pair(R.drawable.visibility_24px, "Password is visible")
        }else{
            Pair(R.drawable.visibility_off_24px, "Password is not visible")
        }

    val (emailFocReq, pwFocReq, buttonFocReq) = remember{ FocusRequester.createRefs() }

    var emptyCheck by remember{ mutableStateOf(false) }

    val emailMalformedError by remember {
        derivedStateOf { email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()}
    }
    val emailEmptyError by remember{
        derivedStateOf { email.isEmpty()&&emptyCheck }
    }
    val passwordEmptyError by remember{
        derivedStateOf { password.isEmpty()&&emptyCheck }
    }
    val passwordMalformedError by remember{ derivedStateOf { password.isNotEmpty()&&!password.isValidPassword() }}

    val emailLabelString =
        if (emailEmptyError) "Email can not be empty"
        else if(emailMalformedError) "Email invalid"
        else "Email"

    val passwordLabelString =
        if (passwordEmptyError) "Password can not be empty"
        else if(passwordMalformedError) "Password invalid"
        else "Password"


    Column(
        modifier = Modifier
            .fillMaxWidth()
    ){
        //email
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocReq)
                .autofill(autofillTypes = listOf(AutofillType.EmailAddress)) {
                    //if(email.isEmpty())pwFocReq.requestFocus()
                    email = it
                },
            colors = textFieldColors,
            value = email,
            onValueChange = {email = it.replace(" ", "")},
            enabled = enabled.value,
            leadingIcon ={
                Image(
                    painter = painterResource(id = R.drawable.mail_24px),
                    contentDescription = "Email",
                    colorFilter = textIconColors?.let{ColorFilter.tint(it)}
                )
            },
            singleLine = true,
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next) ,
            keyboardActions = KeyboardActions(
                onNext = {
                    pwFocReq.requestFocus()
                }
            ),
            label = {Text(emailLabelString)},
            isError = (emailEmptyError||emailMalformedError)

        )
        //Password
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(pwFocReq)
                .autofill(autofillTypes = listOf(AutofillType.Password)) {
                    password = it
                },
            value = password,
            onValueChange = {password = it.replace(" ", "")},
            colors = textFieldColors,
            enabled = enabled.value,
            leadingIcon ={
                Image(
                    painter = painterResource(id = R.drawable.key_24px),
                    contentDescription = "password",
                    colorFilter = textIconColors?.let{ColorFilter.tint(it)}


                )
            },
            trailingIcon = {
                Image(
                    modifier = Modifier
                        .clickable{
                            passwordVisible=!passwordVisible
                        },
                    painter = painterResource(id = visibilityDetails.first),
                    contentDescription = visibilityDetails.second,
                    colorFilter = textIconColors?.let{ColorFilter.tint(it)}
                )},
            visualTransformation =
                if(passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            singleLine = true,
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done) ,
            keyboardActions = KeyboardActions(
                onDone = {
                    buttonFocReq.requestFocus()
                }
            ),
            label = {Text(passwordLabelString)},
            isError = (passwordEmptyError||passwordMalformedError)

        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(buttonFocReq)
                .onFocusChanged {
                    if (it.isFocused) {
                        emptyCheck=true
                        if((emailMalformedError||emailEmptyError||passwordEmptyError||
                                    passwordMalformedError)
                        ){
                            Toast.makeText(
                                context,
                                "Please correct the above errors",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                        else {
                            viewModel.login(
                                email,
                                password,
                                enabled,
                                context
                            )
                        }
                    }
                }
                .focusable(),
            shape = RectangleShape,

            onClick = {
                emptyCheck=true
                if((emailMalformedError||emailEmptyError||passwordEmptyError||
                            passwordMalformedError)
                    ){
                    Toast.makeText(
                        context,
                        "Please correct the above errors",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else{
                viewModel.login(
                    email,
                    password,
                    enabled,
                    context
                )
                    }
            }
        ) {
            Text("Login")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                modifier= Modifier
                    .weight(1f)
                    .padding(4.dp),

                text = "New Here?")
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RectangleShape,
                onClick = { loginState.value = !loginState.value}) {
                    Text("Create Account")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RegisterCard(
    textFieldColors: TextFieldColors = TextFieldDefaults.colors(),
    textIconColors:Color? = null,
    registerState: MutableState<Boolean>,
    viewModel:LoginViewModel
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var firstName by remember {mutableStateOf("")}
    var lastName by remember {mutableStateOf("")}
    var birthdayState = rememberDatePickerState()
    var birthdayDialogueState by remember{ mutableStateOf(false) }
    var email by remember {mutableStateOf("")}
    var password by remember{mutableStateOf("")}
    var confirmPassword by remember{ mutableStateOf("") }
    var enabled = remember{mutableStateOf(true)}
    var passwordVisible by remember{mutableStateOf(false)}
    val interactionSource = remember{ MutableInteractionSource() }

    var emptyCheck by remember{ mutableStateOf(false) }

    val fNameError by remember{
        derivedStateOf { firstName.isEmpty()&&emptyCheck }
    }
    val lNameError by remember{
        derivedStateOf { lastName.isEmpty()&&emptyCheck }
    }
    val birthdayError by remember{
        derivedStateOf { birthdayState.selectedDateMillis==null&&emptyCheck }
    }
    val emailMalformedError by remember {
        derivedStateOf { email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()}
    }
    val emailEmptyError by remember{
        derivedStateOf { email.isEmpty()&&emptyCheck }
    }
    val passwordEmptyError by remember{
        derivedStateOf { password.isEmpty()&&emptyCheck }
    }
    val confirmPasswordEmptyError by remember{
        derivedStateOf { confirmPassword.isEmpty()&&emptyCheck }
    }

    val passwordMalformedError by remember{ derivedStateOf { password.isNotEmpty()&&!password.isValidPassword() }}
    val passwordsMatchError by remember { derivedStateOf { (password!=confirmPassword) } }


    val emailLabelString =
        if (emailEmptyError) "Email can not be empty"
        else if(emailMalformedError) "Email invalid"
        else "Email"

    val passwordLabelString =
        if (passwordEmptyError) "Password can not be empty"
        else if(passwordMalformedError) "Password invalid"
        else "Password"

    val confirmPasswordLabelString =
        if (confirmPasswordEmptyError) "Password can not be empty"
        else if(passwordsMatchError) "Passwords do not match"
        else "Confirm Password"




    val visibilityDetails =
        if(passwordVisible){
            Pair(R.drawable.visibility_24px, "Password is visible")
        }else{
            Pair(R.drawable.visibility_off_24px, "Password is not visible")
        }

    val (firstNameFocReq, lastNameFocReq, bDayFocReq, emailFocReq, pwFocReq, confFocReq, buttonFocReq) =
        remember{ FocusRequester.createRefs() }

    val zoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")


    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        //First name
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(firstNameFocReq)
                .autofill(autofillTypes = listOf(AutofillType.PersonFirstName)) {
                    if(firstName.isEmpty())bDayFocReq.requestFocus()
                    firstName = it
                },
            colors = textFieldColors,
            value = firstName,
            onValueChange = { if(it.length<32)firstName = it },
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.person_24px),
                    contentDescription = "First Name",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            singleLine = true,
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    lastNameFocReq.requestFocus()
                }
            ),
            label = {
                if(fNameError)
                    Text("Name can not be empty")
                else
                    Text("First Name")
            },
            isError = fNameError
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(lastNameFocReq)
                .autofill(autofillTypes = listOf(AutofillType.PersonLastName)) {
                    if(lastName.isEmpty())bDayFocReq.requestFocus()
                    lastName = it
                },
            colors = textFieldColors,
            value = lastName,
            onValueChange = { if(it.length<32) lastName = it },
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.person_24px),
                    contentDescription = "Last Name",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            singleLine = true,
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    bDayFocReq.requestFocus()
                }
            ),
            label = {
                if(lNameError)
                    Text("Name can not be empty")
                else
                    Text("Last Name")
            },
            isError = lNameError
        )
        //birthday
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(bDayFocReq)
                .onFocusChanged {
                    if (it.isFocused) {
                        birthdayDialogueState = true
                    }
                }
                .focusable()
                .clickable {
                    birthdayDialogueState = true
                },
            colors = textFieldColors,
            value = birthdayState.selectedDateMillis?.let {
                formatter.format(Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate())
            }.orEmpty(),
            onValueChange = {},
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.calendar_month_24px),
                    contentDescription = "Birthday",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },

            singleLine = true,
            shape = RectangleShape,
            readOnly = true,
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    emailFocReq.requestFocus()
                }
            ),
            label = {
                if(birthdayError)
                    Text("Birthday can not be empty")
                else
                    Text("Birthday")
            },
            isError = birthdayError
        )
        if (interactionSource.collectIsPressedAsState().value) {
            birthdayDialogueState = true
        }
        if (birthdayDialogueState) {
            DatePickerDialog(
                onDismissRequest = { birthdayDialogueState = !birthdayDialogueState },
                confirmButton = {
                    Button(
                        modifier = Modifier,
                        shape = RectangleShape,

                        onClick = {
                            birthdayDialogueState = !birthdayDialogueState
                            emailFocReq.requestFocus()
                        }) {
                        Text("Confirm")
                    }
                }
            ) {
                DatePicker(state = birthdayState)
            }
        }
        //email
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(emailFocReq)
                .autofill(autofillTypes = listOf(AutofillType.EmailAddress)) {
                    if(email.isEmpty())pwFocReq.requestFocus()
                    email = it
                },
            colors = textFieldColors,
            value = email,
            onValueChange = { email = it.replace(" ", "") },
            //placeholder = { Text("Email") },
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.mail_24px),
                    contentDescription = "Email",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            singleLine = true,
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = {
                    pwFocReq.requestFocus()
                }
            ),
            label = {Text(emailLabelString)},
            isError = (emailEmptyError||emailMalformedError)
        )
        //Password
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(pwFocReq)
                .autofill(autofillTypes = listOf(AutofillType.NewPassword)) {
                    password = it
                    confirmPassword = it
                },
            value = password,
            onValueChange = { password = it.replace(" ", "") },
            colors = textFieldColors,
            placeholder = { Text("Password") },
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.key_24px),
                    contentDescription = "password",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            trailingIcon = {
                Image(
                    modifier = Modifier
                        .clickable {
                            passwordVisible = !passwordVisible
                        },
                    painter = painterResource(id = visibilityDetails.first),
                    contentDescription = visibilityDetails.second,
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
            singleLine = true,
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onDone = {
                    confFocReq.requestFocus()
                    //todo login function
                }
            ),
            label = { Text(passwordLabelString) },
            isError = (passwordEmptyError||passwordMalformedError)


        )
        //Confirm Password
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(confFocReq)
                .autofill(autofillTypes = listOf(AutofillType.NewPassword)) {
                    password = it
                    confirmPassword = it
                },
            value = confirmPassword,
            onValueChange = { confirmPassword = it.replace(" ", "") },
            colors = textFieldColors,
            //placeholder = { Text("Confirm Password") },
            enabled = enabled.value,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.key_24px),
                    contentDescription = "Confirm Password",
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            trailingIcon = {
                Image(
                    modifier = Modifier
                        .clickable {
                            passwordVisible = !passwordVisible
                        },
                    painter = painterResource(id = visibilityDetails.first),
                    contentDescription = visibilityDetails.second,
                    colorFilter = textIconColors?.let { ColorFilter.tint(it) }
                )
            },
            visualTransformation =
            if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            singleLine = true,
            shape = RectangleShape,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    buttonFocReq.requestFocus()
                }
            ),
            isError = (confirmPasswordEmptyError||passwordsMatchError),
            label = {
                Text(confirmPasswordLabelString)
            }


        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(buttonFocReq)
                .onFocusChanged {
                    if (it.isFocused) {
                        emptyCheck=true
                        if((fNameError||lNameError||birthdayError||passwordsMatchError||
                                    passwordMalformedError||emailMalformedError||emailEmptyError||
                                    passwordEmptyError||confirmPasswordEmptyError)){
                            Toast.makeText(
                                context,
                                "Please correct the above errors",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                        else {
                            viewModel.register(
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                birthday = Instant.ofEpochMilli(birthdayState.selectedDateMillis!!)
                                    .atZone(zoneId).toLocalDate(),
                                password = password,
                                enableState = enabled,
                                context = context,
                                coroutineScope = coroutineScope
                            )
                        }
                    }
                }
                .focusable(),
            shape = RectangleShape,

            onClick = {
                emptyCheck=true
                //for some reason putting all the errors into a list and checking with
                // list.contains(true) didnt work. i actually hate this but it works.
                if((fNameError||lNameError||birthdayError||passwordsMatchError||
                            passwordMalformedError||emailMalformedError||emailEmptyError||
                            passwordEmptyError||confirmPasswordEmptyError)){
                    Toast.makeText(
                        context,
                        "Please correct the above errors",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                else {
                    viewModel.register(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        birthday = Instant.ofEpochMilli(birthdayState.selectedDateMillis!!)
                            .atZone(zoneId).toLocalDate(),
                        password = password,
                        enableState = enabled,
                        context = context,
                        coroutineScope = coroutineScope
                    )
                }
            }) {
            Text("Create Account")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),

                text = "Got an account?"
            )
            Button(
                modifier = Modifier
                    .weight(1f),
                shape = RectangleShape,
                onClick = { registerState.value = !registerState.value }) {
                Text("Login")
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    FamilyGiftListTheme {
        val textFieldColors = TextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
        val textIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        val state = remember{mutableStateOf(true)}
        val auth = Firebase.auth
        val navController = rememberNavController()
        val db = Room.databaseBuilder(
            LocalContext.current,
            AppDatabase::class.java, "gift-app-test-database"
        ).build()


        LoginCard(textFieldColors, textIconColor, state, LoginViewModel(auth, db, navController))
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    FamilyGiftListTheme {
        val textFieldColors = TextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
        val textIconColor = MaterialTheme.colorScheme.onPrimaryContainer
        val state = remember{mutableStateOf(true)}
        val auth = Firebase.auth
        val navController = rememberNavController()
        val db = Room.databaseBuilder(
            LocalContext.current,
            AppDatabase::class.java, "gift-app-test-database"
        ).build()

        RegisterCard(textFieldColors, textIconColor, state, LoginViewModel(auth, db, navController))
    }
}