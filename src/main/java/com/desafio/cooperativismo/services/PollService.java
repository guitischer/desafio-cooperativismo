package com.desafio.cooperativismo.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.desafio.cooperativismo.dtos.PollDTO;
import com.desafio.cooperativismo.enums.ErrorMessageEnum;
import com.desafio.cooperativismo.enums.PollStatusEnum;
import com.desafio.cooperativismo.enums.ResultEnum;
import com.desafio.cooperativismo.enums.VoteEnum;
import com.desafio.cooperativismo.exceptions.InvalidParameterException;
import com.desafio.cooperativismo.exceptions.MissingParameterException;
import com.desafio.cooperativismo.exceptions.ResourceNotFoundException;
import com.desafio.cooperativismo.models.Poll;
import com.desafio.cooperativismo.models.Topic;
import com.desafio.cooperativismo.repositories.PollRepository;
import com.desafio.cooperativismo.repositories.TopicRepository;

@Service
public class PollService {

  @Autowired
  PollRepository pollRepository;

  @Autowired
  TopicRepository topicRepository;

  /**
   * Método que retorna todas as sessões de votação
   * 
   * @return List<Poll> lista com as sessões de votação (Poll)
   */
  public List<Poll> getPolls() {
    return pollRepository.findAll();
  }

  /**
   * Método para realizar a criação da sessão de votação (Poll)
   * 
   * @param pollDTO DTO (Data Transfer Object) da sessão de votação (Poll).
   * 
   * @throws MissingParameterException caso algum parâmetro obrigatório não seja
   *                                   enviado pelo DTO
   * @throws ResourceNotFoundException caso a pauta (Topic) não exista
   * @throws InvalidParameterException caso alguma regra de negócio não cumpra com
   *                                   o que deveria
   */
  public void createPoll(PollDTO pollDTO) {  
    requiredTopicValidation(pollDTO);

    Topic topic = checkIfTopicExists(pollDTO.getTopicId());

    validateTopicRelationship(topic);

    var poll = new Poll();
    poll.setTopic(topic);

    if (pollDTO.getEndAt() == null) {
      LocalDateTime today = LocalDateTime.now().plus(Duration.of(1, ChronoUnit.MINUTES));
      poll.setEndAt(today);
    } else {
      poll.setEndAt(pollDTO.getEndAt());
    }

    checkIfPollEndIsNotInThePast(poll);

    pollRepository.save(poll);
  }

  /**
   * Método que realiza a somatória das votações da sessão de votação (Poll)
   * enviada como parâmetro e retorna o resultado no formato de Enum
   * 
   * @param pollId identificador único da sessão de votação (Poll)
   * @throws ResourceNotFoundException caso a sessão de votação (Poll) não exista
   * @return ResultEnum caso a maioria dos votos tenha sido SIM, retornará
   *         VoteEnum.APPROVED, caso contrário, retornará VoteEnum.DISAPPROVED
   */
  public ResultEnum getResult(Long pollId) {
    Poll poll = pollRepository.findById(pollId)
        .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageEnum.POLL_NOT_FOUND));
    Long upvotes = poll.getVotes().stream().filter(vote -> vote.getVote().equals(VoteEnum.YES)).count();
    Long downvotes = poll.getVotes().stream().filter(vote -> vote.getVote().equals(VoteEnum.NO)).count();
    ResultEnum result;

    if (upvotes > downvotes) {
      result = ResultEnum.APPROVED;
    } else if (upvotes < downvotes) {
      result = ResultEnum.DISAPPROVED;
    } else {
      result = ResultEnum.TIE;
    }

    return result;
  }

  /**
   * Método que verifica se a sessão de votação (Poll) está aberta ou fechada a
   * partir da data/hora fim definida na criação da sessão de votação.
   * 
   * @param pollId identificador único da sessão de votação (Poll)
   * @throws ResourceNotFoundException caso a sessão de votação (Poll) não exista
   * @return PollStatusEnum retornará PollStatusEnum.POLL_CLOSED caso a sessão de
   *         votação (Poll) tenha acabado, caso contrário,
   *         PollStatusEnum.POLL_OPENED
   */
  public PollStatusEnum getStatus(Long pollId) {
    Poll poll = pollRepository.findById(pollId)
        .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageEnum.POLL_NOT_FOUND));

    if (LocalDateTime.now().isAfter(poll.getEndAt())) {
      return PollStatusEnum.POLL_CLOSED;
    }

    return PollStatusEnum.POLL_OPENED;
  }

  private void checkIfPollEndIsNotInThePast(Poll poll) {
    if (poll.getEndAt() != null && poll.getEndAt().isBefore(LocalDateTime.now())) {
      throw new InvalidParameterException(ErrorMessageEnum.POLL_IN_PAST.getMessage());
    }
  }

  private Topic checkIfTopicExists(Long topicId) {
    Optional<Topic> topic = topicRepository.findById(topicId);
    if (!topic.isPresent()) {
      throw new ResourceNotFoundException(ErrorMessageEnum.TOPIC_NOT_FOUND);
    }
    return topic.get();
  }

  private void requiredTopicValidation(PollDTO pollDto) {
    if (pollDto.getTopicId() == null) {
      throw new MissingParameterException(ErrorMessageEnum.REQUIRED_TOPIC_FIELD);
    }
  }

  private void validateTopicRelationship(Topic topic) {
    List<Poll> polls = pollRepository.findAllOpenedPollsByTopic(topic, LocalDateTime.now());
    if (polls.size() > 0) {
      throw new InvalidParameterException(ErrorMessageEnum.POLL_WITH_TOPIC_ALREADY_RUNNING.getMessage());
    }
  }
}
