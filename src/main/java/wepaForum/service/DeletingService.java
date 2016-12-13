package wepaForum.service;

import java.util.Iterator;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wepaForum.domain.Account;
import wepaForum.domain.ForumCategory;
import wepaForum.domain.Message;
import wepaForum.domain.SubForum;
import wepaForum.domain.Topic;
import wepaForum.repository.AccountRepository;
import wepaForum.repository.ForumCategoryRepository;
import wepaForum.repository.ForumRepository;
import wepaForum.repository.MessageRepository;
import wepaForum.repository.SubForumRepository;
import wepaForum.repository.TopicRepository;

@Service
public class DeletingService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ForumRepository forumRepository;
    @Autowired
    private ForumCategoryRepository forumCategoryRepository;
    @Autowired
    private SubForumRepository subForumRepository;
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private MessageRepository messageRepository;
    
    @Transactional
    public void deleteForumCategory(Long forumId, Long id) {
        ForumCategory category = forumCategoryRepository.findOne(id);
        //Poistetaan ensin kaikki jäämät.
        for (Iterator<SubForum> itSub = category.getSubForums().iterator(); itSub.hasNext();) {
            SubForum subForum = itSub.next();
            for (Iterator<Topic> itTopic = subForum.getTopics().iterator(); itTopic.hasNext();) {
                Topic topic = itTopic.next();
                for (Iterator<Message> itMessage = topic.getMessages().iterator(); itMessage.hasNext();) {
                    Message message = itMessage.next();
                    itMessage.remove();
                    messageRepository.delete(message.getId());
                }
                for (Account account : topic.getAccounts()) {
                    account.getTopics().remove(topic);                    
                }
                itTopic.remove();
                topicRepository.delete(topic.getId());
            }
            itSub.remove();
            subForumRepository.delete(subForum.getId());
        }
        forumRepository.findOne(forumId).getForumCategories().remove(category);
        forumCategoryRepository.delete(id);
    }
    
    @Transactional
    public void deleteSubForum(Long categoryId, Long id) {
        SubForum subForum = subForumRepository.findOne(id);
        //Poistetaan ensin kaikki jäämät.
        for (Iterator<Topic> itTopic = subForum.getTopics().iterator(); itTopic.hasNext();) {
            Topic topic = itTopic.next();
            for (Iterator<Message> itMessage = topic.getMessages().iterator(); itMessage.hasNext();) {
                Message message = itMessage.next();
                itMessage.remove();
                messageRepository.delete(message.getId());
            }
            for (Account account : topic.getAccounts()) {
                account.getTopics().remove(topic);                    
            }
            itTopic.remove();
            topicRepository.delete(topic.getId());
        }
        forumCategoryRepository.findOne(categoryId).getSubForums().remove(subForum);
        subForumRepository.delete(id);
    }
    
    @Transactional
    public void deleteTopic(Long subforumId, Long id) {
        Topic topic = topicRepository.findOne(id);
        //Poistetaan ensin kaikki jäämät.
        for (Iterator<Message> itMessage = topic.getMessages().iterator(); itMessage.hasNext();) {
            Message message = itMessage.next();
            itMessage.remove();
            messageRepository.delete(message.getId());
        }
        for (Account account : topic.getAccounts()) {
            account.getTopics().remove(topic);                    
        }
        subForumRepository.findOne(subforumId).deleteTopic(topic);
        topicRepository.delete(id);
    }
    
    @Transactional
    public void deleteMessage(Long topicId, Long id) {
        Message message = messageRepository.findOne(id);
        topicRepository.findOne(topicId).deleteMessage(message);
        messageRepository.delete(id);
    }
}
