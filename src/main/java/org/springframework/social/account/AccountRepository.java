package org.springframework.social.account;

import java.util.List;

import org.springframework.social.account.Account;

public interface AccountRepository {

	Account createAccount(Person person) throws EmailAlreadyOnFileException;

	Account authenticate(String username, String password) throws UsernameNotFoundException, InvalidPasswordException;

	void changePassword(Long accountId, String password);
	
	Account findById(Long id);

	Account findByUsername(String username) throws UsernameNotFoundException;
	
	Account findByConnectedAccount(String provider, String accessToken) throws ConnectedAccountNotFoundException; // TODO exception case where accessToken is valid

	List<Account> findFriendAccounts(String provider, List<String> friendIds);
	
	void connect(Long id, String provider, String accessToken, String accountId) throws AccountAlreadyConnectedException;

	boolean isConnected(Long id, String provider);

	void disconnect(Long id, String provider);

	void markProfilePictureSet(Long id);
		
}
