package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

import javax.security.auth.login.AccountExpiredException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    /**
     * Registers a new account if the username is unique and password is valid.
     *
     * @param account the account to register
     * @return HTTP 200 with the registered account or appropriate error status
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        if (!accountService.isValid(account)) {
            return ResponseEntity.badRequest().build();
        } else if (accountService.existsByUsername(account.getUsername())) {
            return ResponseEntity.status(409).build();
        }
        Account registered = accountService.save(account);
        return ResponseEntity.ok(registered);
    }

    /**
     * Authenticates a user by verifying the provided username and password.
     *
     * @param account the account credentials to log in
     * @return HTTP 200 with account if successful, 401 if unauthorized
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        Account loggedIn = accountService.findByUsernameAndPassword(account.getUsername(), account.getPassword());
        if (loggedIn == null) {
            return ResponseEntity.status(401).build();
        } else {
            return ResponseEntity.ok(loggedIn);
        }
    }

    /**
     * Posts a new message if the text is valid and the account exists.
     *
     * @param message the message to post
     * @return HTTP 200 with the created message or 400 on error
     */
    @PostMapping("/messages")
    public ResponseEntity<?> postMessage(@RequestBody Message message) {
        if (!messageService.isValid(message) || !accountService.existsById(message.getPostedBy())) {
            return ResponseEntity.badRequest().build();
        }
        Message created = messageService.save(message);
        return ResponseEntity.ok(created);
    }

    /**
     * Retrieves all messages from the database.
     *
     * @return HTTP 200 with the list of all messages
     */
    @GetMapping("/messages")
    public ResponseEntity<?> getMessages() {
        return ResponseEntity.ok(messageService.findAll());
    }

    /**
     * Retrieves a single message by its ID.
     *
     * @param message_id the ID of the message to retrieve
     * @return HTTP 200 with the message or null if not found
     */
    @GetMapping("/messages/{message_id}")
    public ResponseEntity<?> getMessageById(@PathVariable int message_id) {
        return ResponseEntity.ok(messageService.findById(message_id));
    }

    /**
     * Deletes a message by its ID if it exists.
     *
     * @param message_id the ID of the message to delete
     * @return HTTP 200 with number of rows deleted or empty response
     */
    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<?> deleteMessageById(@PathVariable int message_id) {
        int rows = messageService.deleteById(message_id);
        if (rows != 0) {
            return ResponseEntity.ok(rows);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the text of a message by ID if the message exists and text is valid.
     *
     * @param message_id the ID of the message to update
     * @param message the new message content
     * @return HTTP 200 with number of rows updated or 400 on error
     */
    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<?> updateMessageById(@PathVariable int message_id, @RequestBody Message message) {
        if (!messageService.existsById(message_id) || message.getMessageText().isBlank() || message.getMessageText() == null || message.getMessageText().length() > 255) {
            return ResponseEntity.status(400).build();
        }
        int rows = messageService.updateById(message_id, message.getMessageText());
        if (rows != 0) {
            return ResponseEntity.ok(rows);
        }
        return ResponseEntity.ok().build();        
    }

    /**
     * Retrieves all messages posted by a specific account ID.
     *
     * @param account_id the ID of the account to filter messages by
     * @return HTTP 200 with list of messages posted by the account
     */
    @GetMapping("accounts/{account_id}/messages")
    public ResponseEntity<?> getMessageByAccount(@PathVariable int account_id) {
        return ResponseEntity.ok(messageService.findByPostedBy(account_id));
    }
}
