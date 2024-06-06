package cricket.merstham.graphql.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.graphql.dto.PlayCricketTeam;
import cricket.merstham.graphql.dto.PlayCricketTeamResponse;
import cricket.merstham.graphql.entity.FixtureEntity;
import cricket.merstham.graphql.entity.TeamEntity;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class PlayCricketServiceTest {

    private static final String API_TOKEN = UUID.randomUUID().toString();
    private static final int SITE_ID = 1234;
    private static final int OTHER_SITE_ID = 4321;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String DETAIL =
            "{"
                    + "            \"id\": 1,"
                    + "            \"status\": \"New\","
                    + "            \"published\": \"Yes\","
                    + "            \"last_updated\": \"30/05/2024\","
                    + "            \"league_name\": \"Surrey Cricket Championship\","
                    + "            \"league_id\": \"2\","
                    + "            \"competition_name\": \"Division 1\","
                    + "            \"competition_id\": \"3\","
                    + "            \"competition_type\": \"League\","
                    + "            \"match_type\": \"Limited Overs\","
                    + "            \"game_type\": \"Standard\","
                    + "            \"countdown_cricket\": \"no\","
                    + "            \"match_id\": \"1\","
                    + "            \"match_date\": \"12/05/2024\","
                    + "            \"match_time\": \"13:30\","
                    + "            \"ground_name\": \"Main Pitch\","
                    + "            \"ground_id\": \"4\","
                    + "            \"home_team_name\": \"1st XI\","
                    + "            \"home_team_id\": \"5\","
                    + "            \"home_club_name\": \"Home CC\","
                    + "            \"home_club_id\": \"6\","
                    + "            \"away_team_name\": \"1st XI\","
                    + "            \"away_team_id\": \"7\","
                    + "            \"away_club_name\": \"Away CC\","
                    + "            \"away_club_id\": \"8\","
                    + "            \"umpire_1_name\": \"\","
                    + "            \"umpire_1_id\": \"\","
                    + "            \"umpire_2_name\": \"\","
                    + "            \"umpire_2_id\": \"\","
                    + "            \"umpire_3_name\": \"\","
                    + "            \"umpire_3_id\": \"\","
                    + "            \"referee_name\": \"\","
                    + "            \"referee_id\": \"\","
                    + "            \"scorer_1_name\": \"\","
                    + "            \"scorer_1_id\": \"\","
                    + "            \"scorer_2_name\": \"\","
                    + "            \"scorer_2_id\": \"\","
                    + "            \"toss_won_by_team_id\": \"7\","
                    + "            \"toss\": \"Away CC - 1st XI won the toss and elected to field\","
                    + "            \"batted_first\": \"5\","
                    + "            \"no_of_overs\": \"40\","
                    + "            \"balls_per_innings\": \"\","
                    + "            \"no_of_innings\": \"1\","
                    + "            \"no_of_days\": \"1\","
                    + "            \"no_of_players\": \"11\","
                    + "            \"no_of_reserves\": \"1\","
                    + "            \"result\": \"W\","
                    + "            \"result_description\": \"Away CC - 1st XI - Won\","
                    + "            \"result_applied_to\": \"7\","
                    + "            \"match_notes\": \"\","
                    + "            \"points\": ["
                    + "                {"
                    + "                    \"team_id\": 5,"
                    + "                    \"game_points\": \"1\","
                    + "                    \"penalty_points\": \"0.0\","
                    + "                    \"bonus_points_together\": \"0.0\","
                    + "                    \"bonus_points_batting\": \"0.0\","
                    + "                    \"bonus_points_bowling\": \"0.0\","
                    + "                    \"bonus_points_2nd_innings_together\": \"\""
                    + "                },"
                    + "                {"
                    + "                    \"team_id\": 7,"
                    + "                    \"game_points\": \"4\","
                    + "                    \"penalty_points\": \"0.0\","
                    + "                    \"bonus_points_together\": \"0.0\","
                    + "                    \"bonus_points_batting\": \"0.0\","
                    + "                    \"bonus_points_bowling\": \"0.0\","
                    + "                    \"bonus_points_2nd_innings_together\": \"\""
                    + "                }"
                    + "            ],"
                    + "            \"match_result_types\": ["
                    + "                ["
                    + "                    \"Home CC - 1st XI - Won\","
                    + "                    \"942915#5\""
                    + "                ],"
                    + "                ["
                    + "                    \"Away CC - 1st XI - Won\","
                    + "                    \"942915#7\""
                    + "                ],"
                    + "                ["
                    + "                    \"Tied\","
                    + "                    8"
                    + "                ],"
                    + "                ["
                    + "                    \"Cancelled\","
                    + "                    9"
                    + "                ],"
                    + "                ["
                    + "                    \"Abandoned\","
                    + "                    10"
                    + "                ],"
                    + "                ["
                    + "                    \"Home CC - 1st XI - Conceded\","
                    + "                    \"942919#5\""
                    + "                ],"
                    + "                ["
                    + "                    \"Away CC - 1st XI - Conceded\","
                    + "                    \"942920#7\""
                    + "                ],"
                    + "                ["
                    + "                    \"Match In Progress\","
                    + "                    11"
                    + "                ]"
                    + "            ],"
                    + "            \"starting_runs\": \"200\","
                    + "            \"dismissal_penalty\": \"5\","
                    + "            \"players\": ["
                    + "                {"
                    + "                    \"home_team\": ["
                    + "                        {"
                    + "                            \"position\": 1,"
                    + "                            \"player_name\": \"A B\","
                    + "                            \"player_id\": 101,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 2,"
                    + "                            \"player_name\": \"C D\","
                    + "                            \"player_id\": 102,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 3,"
                    + "                            \"player_name\": \"E F\","
                    + "                            \"player_id\": 103,"
                    + "                            \"captain\": true,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 4,"
                    + "                            \"player_name\": \"G H\","
                    + "                            \"player_id\": 104,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 5,"
                    + "                            \"player_name\": \"I J\","
                    + "                            \"player_id\": 105,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 6,"
                    + "                            \"player_name\": \"K L\","
                    + "                            \"player_id\": 106,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": true"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 7,"
                    + "                            \"player_name\": \"M N\","
                    + "                            \"player_id\": 107,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 8,"
                    + "                            \"player_name\": \"O P\","
                    + "                            \"player_id\": 108,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 9,"
                    + "                            \"player_name\": \"Q R\","
                    + "                            \"player_id\": 109,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 10,"
                    + "                            \"player_name\": \"S T\","
                    + "                            \"player_id\": 110,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 11,"
                    + "                            \"player_name\": \"U V\","
                    + "                            \"player_id\": 111,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        }"
                    + "                    ]"
                    + "                },"
                    + "                {"
                    + "                    \"away_team\": ["
                    + "                        {"
                    + "                            \"position\": 1,"
                    + "                            \"player_name\": \"A A\","
                    + "                            \"player_id\": 201,"
                    + "                            \"captain\": true,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 2,"
                    + "                            \"player_name\": \"B B\","
                    + "                            \"player_id\": 202,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 3,"
                    + "                            \"player_name\": \"C C\","
                    + "                            \"player_id\": 203,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 4,"
                    + "                            \"player_name\": \"D D\","
                    + "                            \"player_id\": 204,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 5,"
                    + "                            \"player_name\": \"E E\","
                    + "                            \"player_id\": 205,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 6,"
                    + "                            \"player_name\": \"F F\","
                    + "                            \"player_id\": 206,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 7,"
                    + "                            \"player_name\": \"G G\","
                    + "                            \"player_id\": 207,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": true"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 8,"
                    + "                            \"player_name\": \"H H\","
                    + "                            \"player_id\": 208,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 9,"
                    + "                            \"player_name\": \"I I\","
                    + "                            \"player_id\": 209,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 10,"
                    + "                            \"player_name\": \"J J\","
                    + "                            \"player_id\": 210,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": 11,"
                    + "                            \"player_name\": \"K K\","
                    + "                            \"player_id\": 211,"
                    + "                            \"captain\": false,"
                    + "                            \"wicket_keeper\": false"
                    + "                        }"
                    + "                    ]"
                    + "                }"
                    + "            ],"
                    + "            \"innings\": ["
                    + "                {"
                    + "                    \"team_batting_name\": \"Away CC - 1st XI\","
                    + "                    \"team_batting_id\": \"7\","
                    + "                    \"innings_number\": 1,"
                    + "                    \"extra_byes\": \"2\","
                    + "                    \"extra_leg_byes\": \"0\","
                    + "                    \"extra_wides\": \"44\","
                    + "                    \"extra_no_balls\": \"2\","
                    + "                    \"extra_penalty_runs\": \"0\","
                    + "                    \"penalties_runs_awarded_in_other_innings\": \"0\","
                    + "                    \"total_extras\": \"48\","
                    + "                    \"runs\": \"92\","
                    + "                    \"wickets\": \"3\","
                    + "                    \"overs\": \"16.0\","
                    + "                    \"balls\": \"\","
                    + "                    \"declared\": false,"
                    + "                    \"forfeited_innings\": false,"
                    + "                    \"revised_target_runs\": \"\","
                    + "                    \"revised_target_overs\": \"\","
                    + "                    \"revised_target_balls\": \"\","
                    + "                    \"bat\": ["
                    + "                        {"
                    + "                            \"position\": \"1\","
                    + "                            \"batsman_name\": \"A A\","
                    + "                            \"batsman_id\": \"201\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"4\","
                    + "                            \"fours\": \"0\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"11\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"2\","
                    + "                            \"batsman_name\": \"B B\","
                    + "                            \"batsman_id\": \"202\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"3\","
                    + "                            \"fours\": \"0\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"13\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"3\","
                    + "                            \"batsman_name\": \"C C\","
                    + "                            \"batsman_id\": \"203\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"5\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"11\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"4\","
                    + "                            \"batsman_name\": \"D D\","
                    + "                            \"batsman_id\": \"204\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"9\","
                    + "                            \"fours\": \"2\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"13\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"5\","
                    + "                            \"batsman_name\": \"E E\","
                    + "                            \"batsman_id\": \"205\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"3\","
                    + "                            \"fours\": \"0\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"7\","
                    + "                            \"times_out\": \"1\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"6\","
                    + "                            \"batsman_name\": \"F F\","
                    + "                            \"batsman_id\": \"206\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"11\","
                    + "                            \"fours\": \"2\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"17\","
                    + "                            \"times_out\": \"1\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"7\","
                    + "                            \"batsman_name\": \"G G\","
                    + "                            \"batsman_id\": \"207\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"4\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"13\","
                    + "                            \"times_out\": \"1\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"8\","
                    + "                            \"batsman_name\": \"H H\","
                    + "                            \"batsman_id\": \"208\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"5\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"11\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"9\","
                    + "                            \"batsman_name\": \"I I\","
                    + "                            \"batsman_id\": \"209\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"5\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"11\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"10\","
                    + "                            \"batsman_name\": \"J J\","
                    + "                            \"batsman_id\": \"210\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"5\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"11\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"11\","
                    + "                            \"batsman_name\": \"K K\","
                    + "                            \"batsman_id\": \"211\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"5\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"11\","
                    + "                            \"times_out\": \"0\""
                    + "                        }"
                    + "                    ],"
                    + "                    \"fow\": [],"
                    + "                    \"bowl\": ["
                    + "                        {"
                    + "                            \"bowler_name\": \"A B\","
                    + "                            \"bowler_id\": \"101\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"12\","
                    + "                            \"wides\": \"10\","
                    + "                            \"wickets\": \"0\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"C D\","
                    + "                            \"bowler_id\": \"102\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"21\","
                    + "                            \"wides\": \"10\","
                    + "                            \"wickets\": \"0\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"E F\","
                    + "                            \"bowler_id\": \"103\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"4\","
                    + "                            \"wides\": \"2\","
                    + "                            \"wickets\": \"1\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"G H\","
                    + "                            \"bowler_id\": \"104\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"12\","
                    + "                            \"wides\": \"8\","
                    + "                            \"wickets\": \"0\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"I J\","
                    + "                            \"bowler_id\": \"105\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"12\","
                    + "                            \"wides\": \"4\","
                    + "                            \"wickets\": \"2\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"K L\","
                    + "                            \"bowler_id\": \"106\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"10\","
                    + "                            \"wides\": \"8\","
                    + "                            \"wickets\": \"0\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"M N\","
                    + "                            \"bowler_id\": \"107\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"15\","
                    + "                            \"wides\": \"2\","
                    + "                            \"wickets\": \"0\","
                    + "                            \"no_balls\": \"2\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"O P\","
                    + "                            \"bowler_id\": \"108\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"1\","
                    + "                            \"runs\": \"4\","
                    + "                            \"wides\": \"0\","
                    + "                            \"wickets\": \"0\","
                    + "                            \"no_balls\": \"0\""
                    + "                        }"
                    + "                    ]"
                    + "                },"
                    + "                {"
                    + "                    \"team_batting_name\": \"Home CC - 1st XI\","
                    + "                    \"team_batting_id\": \"5\","
                    + "                    \"innings_number\": 1,"
                    + "                    \"extra_byes\": \"2\","
                    + "                    \"extra_leg_byes\": \"0\","
                    + "                    \"extra_wides\": \"9\","
                    + "                    \"extra_no_balls\": \"10\","
                    + "                    \"extra_penalty_runs\": \"0\","
                    + "                    \"penalties_runs_awarded_in_other_innings\": \"0\","
                    + "                    \"total_extras\": \"21\","
                    + "                    \"runs\": \"51\","
                    + "                    \"wickets\": \"9\","
                    + "                    \"overs\": \"16.0\","
                    + "                    \"balls\": \"\","
                    + "                    \"declared\": false,"
                    + "                    \"forfeited_innings\": false,"
                    + "                    \"revised_target_runs\": \"\","
                    + "                    \"revised_target_overs\": \"\","
                    + "                    \"revised_target_balls\": \"\","
                    + "                    \"bat\": ["
                    + "                        {"
                    + "                            \"position\": \"1\","
                    + "                            \"batsman_name\": \"A B\","
                    + "                            \"batsman_id\": \"101\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"2\","
                    + "                            \"fours\": \"0\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"7\","
                    + "                            \"times_out\": \"1\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"2\","
                    + "                            \"batsman_name\": \"C D\","
                    + "                            \"batsman_id\": \"102\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"7\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"17\","
                    + "                            \"times_out\": \"1\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"3\","
                    + "                            \"batsman_name\": \"E F\","
                    + "                            \"batsman_id\": \"103\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"8\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"10\","
                    + "                            \"times_out\": \"1\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"4\","
                    + "                            \"batsman_name\": \"G H\","
                    + "                            \"batsman_id\": \"104\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"2\","
                    + "                            \"fours\": \"0\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"14\","
                    + "                            \"times_out\": \"1\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"5\","
                    + "                            \"batsman_name\": \"I J\","
                    + "                            \"batsman_id\": \"105\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"2\","
                    + "                            \"fours\": \"0\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"10\","
                    + "                            \"times_out\": \"2\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"6\","
                    + "                            \"batsman_name\": \"K L\","
                    + "                            \"batsman_id\": \"106\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"0\","
                    + "                            \"fours\": \"0\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"14\","
                    + "                            \"times_out\": \"2\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"7\","
                    + "                            \"batsman_name\": \"M N\","
                    + "                            \"batsman_id\": \"107\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"1\","
                    + "                            \"fours\": \"0\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"14\","
                    + "                            \"times_out\": \"1\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"8\","
                    + "                            \"batsman_name\": \"O P\","
                    + "                            \"batsman_id\": \"108\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"8\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"10\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"9\","
                    + "                            \"batsman_name\": \"Q R\","
                    + "                            \"batsman_id\": \"109\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"8\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"10\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"10\","
                    + "                            \"batsman_name\": \"S T\","
                    + "                            \"batsman_id\": \"110\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"8\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"10\","
                    + "                            \"times_out\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"position\": \"11\","
                    + "                            \"batsman_name\": \"U V\","
                    + "                            \"batsman_id\": \"111\","
                    + "                            \"how_out\": null,"
                    + "                            \"fielder_name\": \"\","
                    + "                            \"fielder_id\": \"\","
                    + "                            \"bowler_name\": \"\","
                    + "                            \"bowler_id\": \"\","
                    + "                            \"runs\": \"8\","
                    + "                            \"fours\": \"1\","
                    + "                            \"sixes\": \"0\","
                    + "                            \"balls\": \"10\","
                    + "                            \"times_out\": \"0\""
                    + "                        }"
                    + "                    ],"
                    + "                    \"fow\": [],"
                    + "                    \"bowl\": ["
                    + "                        {"
                    + "                            \"bowler_name\": \"A A\","
                    + "                            \"bowler_id\": \"201\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"1\","
                    + "                            \"runs\": \"3\","
                    + "                            \"wides\": \"2\","
                    + "                            \"wickets\": \"2\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"B B\","
                    + "                            \"bowler_id\": \"202\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"12\","
                    + "                            \"wides\": \"0\","
                    + "                            \"wickets\": \"0\","
                    + "                            \"no_balls\": \"4\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"C C\","
                    + "                            \"bowler_id\": \"203\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"1\","
                    + "                            \"runs\": \"3\","
                    + "                            \"wides\": \"0\","
                    + "                            \"wickets\": \"1\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"D D\","
                    + "                            \"bowler_id\": \"204\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"7\","
                    + "                            \"wides\": \"0\","
                    + "                            \"wickets\": \"1\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"E E\","
                    + "                            \"bowler_id\": \"205\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"8\","
                    + "                            \"wides\": \"2\","
                    + "                            \"wickets\": \"1\","
                    + "                            \"no_balls\": \"4\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"F F\","
                    + "                            \"bowler_id\": \"206\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"1\","
                    + "                            \"runs\": \"2\","
                    + "                            \"wides\": \"0\","
                    + "                            \"wickets\": \"2\","
                    + "                            \"no_balls\": \"2\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"G G\","
                    + "                            \"bowler_id\": \"207\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"0\","
                    + "                            \"runs\": \"12\","
                    + "                            \"wides\": \"5\","
                    + "                            \"wickets\": \"1\","
                    + "                            \"no_balls\": \"0\""
                    + "                        },"
                    + "                        {"
                    + "                            \"bowler_name\": \"H H\","
                    + "                            \"bowler_id\": \"208\","
                    + "                            \"overs\": \"2.0\","
                    + "                            \"maidens\": \"1\","
                    + "                            \"runs\": \"2\","
                    + "                            \"wides\": \"0\","
                    + "                            \"wickets\": \"0\","
                    + "                            \"no_balls\": \"0\""
                    + "                        }"
                    + "                    ]"
                    + "                }"
                    + "            ]"
                    + "        }";

    private static final List<PlayCricketTeam> TEAMS =
            List.of(
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Sat 1st XI")
                            .lastUpdated(LocalDate.now())
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(OTHER_SITE_ID)
                            .teamName("Sat 1st XI")
                            .lastUpdated(LocalDate.now().minus(1, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Sat 2nd XI")
                            .lastUpdated(LocalDate.now().minus(2, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Sun 1st XI")
                            .lastUpdated(LocalDate.now().minus(3, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Sun 2nd XI")
                            .lastUpdated(LocalDate.now().minus(4, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("inactive")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(OTHER_SITE_ID)
                            .teamName("Sat 2nd XI")
                            .lastUpdated(LocalDate.now().minus(5, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Other")
                            .lastUpdated(LocalDate.now().minus(6, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("Indoor Team")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Client client = mock(Client.class);
    private final PlayCricketService service =
            new PlayCricketService(client, API_TOKEN, SITE_ID, MAPPER);
    private JsonNode detail;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        detail = MAPPER.readTree(DETAIL);
    }

    @Test
    void shouldCorrectlyGetTeamsWithNoLastUpdate() {
        var expectedTeams = TEAMS.stream().filter(t -> t.getSiteId() == SITE_ID).toList();
        var webTarget = spy(WebTarget.class);
        var builder = spy(Invocation.Builder.class);
        var request = spy(Invocation.class);
        when(client.target(any(UriBuilder.class))).thenReturn(webTarget);
        when(webTarget.request()).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builder);
        when(builder.buildGet()).thenReturn(request);
        when(request.invoke(PlayCricketTeamResponse.class))
                .thenReturn(PlayCricketTeamResponse.builder().teams(TEAMS).build());

        var result = service.getTeams();

        assertThat(result).hasSize(expectedTeams.size());

        for (int i = 0; i < result.size(); i++) {
            assertThat(result.get(i)).isEqualTo(expectedTeams.get(i));
        }
    }

    @Test
    void getPlayersCorrectEvaluatesForHomeFixture() {
        var entity =
                FixtureEntity.builder()
                        .id(1)
                        .homeAway("HOME")
                        .opposition("Away CC")
                        .date(LocalDate.now())
                        .start(LocalTime.of(13, 0))
                        .team(TeamEntity.builder().id(5).name("1st XI").build())
                        .detail(detail)
                        .build();

        var players = service.getPlayers(entity);
        assertThat(players).hasSize(11);
        assertThat(players.stream().map(n -> n.get("player_id").asInt()))
                .containsExactlyElementsOf(
                        List.of(101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111));
    }

    @Test
    void getPlayersCorrectEvaluatesForAwayFixture() {
        var entity =
                FixtureEntity.builder()
                        .id(1)
                        .homeAway("AWAY")
                        .opposition("Home CC")
                        .date(LocalDate.now())
                        .start(LocalTime.of(13, 0))
                        .team(TeamEntity.builder().id(7).name("1st XI").build())
                        .detail(detail)
                        .build();

        var players = service.getPlayers(entity);
        assertThat(players).hasSize(11);
        assertThat(players.stream().map(n -> n.get("player_id").asInt()))
                .containsExactlyElementsOf(
                        List.of(201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211));
    }

    @Test
    void getPlayersCorrectEvaluatesWhenPlayersNotSpecified() throws JsonProcessingException {
        var entity =
                FixtureEntity.builder()
                        .id(1)
                        .homeAway("AWAY")
                        .opposition("Home CC")
                        .date(LocalDate.now())
                        .start(LocalTime.of(13, 0))
                        .team(TeamEntity.builder().id(7).name("1st XI").build())
                        .detail(MAPPER.readTree("{\"players\": []}"))
                        .build();

        var players = service.getPlayers(entity);
        assertThat(players).isEmpty();
    }

    @Test
    void getBattingReturnsBattingStatsWhenAway() {
        var entity =
                FixtureEntity.builder()
                        .id(1)
                        .homeAway("AWAY")
                        .opposition("Home CC")
                        .date(LocalDate.now())
                        .start(LocalTime.of(13, 0))
                        .team(TeamEntity.builder().id(7).name("1st XI").build())
                        .detail(detail)
                        .build();

        var result = service.getBatting(entity, 201);
        assertThat(result).isNotNull();
        assertThat(result.get("runs").asText()).isEqualTo("4");
        assertThat(result.get("balls").asText()).isEqualTo("11");
        assertThat(result.get("fours").asText()).isEqualTo("0");
        assertThat(result.get("sixes").asText()).isEqualTo("0");
    }

    @Test
    void getBattingReturnsBattingStatsWhenHome() {
        var entity =
                FixtureEntity.builder()
                        .id(1)
                        .homeAway("HOME")
                        .opposition("Home CC")
                        .date(LocalDate.now())
                        .start(LocalTime.of(13, 0))
                        .team(TeamEntity.builder().id(5).name("1st XI").build())
                        .detail(detail)
                        .build();

        var result = service.getBatting(entity, 101);
        assertThat(result).isNotNull();
        assertThat(result.get("runs").asText()).isEqualTo("2");
        assertThat(result.get("balls").asText()).isEqualTo("7");
        assertThat(result.get("fours").asText()).isEqualTo("0");
        assertThat(result.get("sixes").asText()).isEqualTo("0");
    }

    @Test
    void getBattingReturnsNullWhenPlayerIdInvalid() {
        var entity =
                FixtureEntity.builder()
                        .id(1)
                        .homeAway("AWAY")
                        .opposition("Home CC")
                        .date(LocalDate.now())
                        .start(LocalTime.of(13, 0))
                        .team(TeamEntity.builder().id(5).name("1st XI").build())
                        .detail(detail)
                        .build();

        var result = service.getBatting(entity, 300);
        assertThat(result).isNull();
    }

    @Test
    void getBowlingReturnsBowlingStatsWhenAway() {
        var entity =
                FixtureEntity.builder()
                        .id(1)
                        .homeAway("AWAY")
                        .opposition("Home CC")
                        .date(LocalDate.now())
                        .start(LocalTime.of(13, 0))
                        .team(TeamEntity.builder().id(7).name("1st XI").build())
                        .detail(detail)
                        .build();

        var result = service.getBowling(entity, 201);
        assertThat(result).isNotNull();
        assertThat(result.get("overs").asText()).isEqualTo("2.0");
        assertThat(result.get("maidens").asText()).isEqualTo("1");
        assertThat(result.get("runs").asText()).isEqualTo("3");
        assertThat(result.get("wickets").asText()).isEqualTo("2");
    }

    @Test
    void getBowlingReturnsBowlingStatsWhenHome() {
        var entity =
                FixtureEntity.builder()
                        .id(1)
                        .homeAway("HOME")
                        .opposition("Home CC")
                        .date(LocalDate.now())
                        .start(LocalTime.of(13, 0))
                        .team(TeamEntity.builder().id(5).name("1st XI").build())
                        .detail(detail)
                        .build();

        var result = service.getBowling(entity, 101);
        assertThat(result).isNotNull();
        assertThat(result.get("overs").asText()).isEqualTo("2.0");
        assertThat(result.get("maidens").asText()).isEqualTo("0");
        assertThat(result.get("runs").asText()).isEqualTo("12");
        assertThat(result.get("wickets").asText()).isEqualTo("0");
    }

    @Test
    void getBowlingReturnsNullWhenPlayerIdInvalid() {
        var entity =
                FixtureEntity.builder()
                        .id(1)
                        .homeAway("HOME")
                        .opposition("Home CC")
                        .date(LocalDate.now())
                        .start(LocalTime.of(13, 0))
                        .team(TeamEntity.builder().id(5).name("1st XI").build())
                        .detail(detail)
                        .build();

        var result = service.getBowling(entity, 300);
        assertThat(result).isNull();
    }
}
