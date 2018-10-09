%% Offline Calculations for the Optimal Reward Strategy
%% Setup
%clc;
load('p-r-d-Tables.mat');
p; %table of probabilities that each city will have a task to another city
r; %table of rewards for tasks from one city to another city
d; %table of distances from one city to another city

costPerKm = 1; %user defined cost per km of transport

c=d*costPerKm; %cost matrix
gamma = .1; %discount factor

% notes:
% state is defined as current city (1-9) and the destination city (1 to 10, 9+1 for no task)
% number of states = 90
% state encoding: s = (cur_city-1)*10 + (dest_city)*1;
% action is defined as delivering a package OR going to any city.
% number of actions = 10
% action encoding: 1 to 9 is to city #, 10 is no task.

%bonus note: reward matrices are actually symmetric it seems, so there's not a huge risk of incorrect indexing. So that's nice. 
%unfortunately probability matrix is not symmetric... 

%% Step 1: State Transition Probability (The fun begins)
T = zeros(90,10,90); %T(s,a,s'): probability of getting to state s' given state s and action a

%cycle through all current states s
for current_from=1:9
    for current_to=1:10
        s = (current_from-1)*10+current_to; %current state        
        
        for future_from=1:9
            for future_to=1:10
                f_s = (future_from-1)*10+future_to; %future state
                
                for a=1:10 %action
                    
                    %rules: 
                    %1) current_to =/= current from
                    %2) future_to =/= future from
                    %3) current_from =/= future from
                    %4) a) if task is taken (a=10) then current_to MUST EQUAL future_from
                    %   b) if action is not taken (a<10), then
                    %     i) action =/= current_from
                    %     ii) action MUST EQUAL future_from
                    %5) all remaining cells *should* be able to be filled from the probability matrix
                    %   a) if future_to is = 10 (no future task), probability = 1-sum(p(future_from),:)
                    %   b) if future_to is <= 9, probability is p(future_from,future_to)
                    if ((current_to ~= current_from) && (future_to ~= future_from) && (current_from ~= future_from)) %rules 1, 2, and 3
                       
                        if ((a==10) && (current_to == future_from))% rule 4a                           
                            if future_to ==10
                                T(s,a,f_s) = 1-sum(p(future_from,:)); %rule 5a
                            else
                                T(s,a,f_s) = p(future_from,future_to);%rule 5b
                            end
                            
                        else % rule 4b
                            
                            if((a~=current_from)&&(a==future_from)) %rules 4bi and 4bii                         
                                if future_to ==10
                                    T(s,a,f_s) = 1-sum(p(future_from,:)); %rule 5a
                                else
                                    T(s,a,f_s) = p(future_from,future_to); %rule 5b
                                end
                            end
                        end                        
                    end
                end
            end
        end
    end
end
%only 9 ends here... not too bad right? o.o'

%% Step 2: State Expected Profit
P = zeros(90,10); %profit for a given state and action
for current_from=1:9
    for current_to=1:10
        s = (current_from-1)*10+current_to; %current state
        for a=1:10 %action            
            %let's start with all NO-TASK actions (1-9)
            if a<10               
                %reward = 0, cost = cost(s_from,a_to)
                P(s,a) = -c(current_from,a);               
            else
                if current_to<10 %don't fill in state where there is no task (to=10)
                    %reward = p(from,to) minus cost(from,to)
                    P(s,a) = r(current_from,current_to)-c(current_from,current_to);                
                end
            end
        end
    end
end

%% Step 3: ???

%% Step 4: Profit.
states_converged = zeros(90,1); %tracks which states have converged already. When all states have converged, break loop
converged = 0; %flag for whether or not the error has converged
Q = zeros(90,10); %infinite horizon value from a given state and action
V = zeros(90,1); %what will eventually store the best values... 
V_temp = V; %used to compare to check for convergence
Best = zeros(90,1); %pluck out the best actions for each state

while ~converged
    for current_from=1:9
        for current_to=1:10
            for a=1:10
                s = (current_from-1)*10+current_to;
                
                %implementing this without a dot product so it's easier to port over to java
                discounted_future = 0;
                for i=1:90
                    discounted_future = discounted_future+T(s,a,i)*V(i);
                end
                Q(s,a) = P(s,a) + gamma*discounted_future; %update value               
            end
            [V_temp(s),Best(s)]=max(Q(s,:)); %keep the best action and value of best action for a given state s
           
            %adding this
            i = 1;
            while current_from == Best(s)
                Qordered = unique(Q(s,:));
                out = Qordered(end-1);
                V_temp(s) = out;
                Best(s) = find(Q(s,:)==V_temp(s));
                i = i+1; %in case of duplicate values. should only ever happen twice)
            end
            %ends here
            if V_temp(s) == V(s)
                states_converged(s) = 1;
            end
            V(s) = V_temp(s);
        end
        %then repeat for all possible states, s. 
    end    
    %check if all states have converged    
    
    if isequal(states_converged, ones(90,1))
        converged = 1;
    end
end

%Decompose Best for faster debugging:
Best2 = zeros(9,10);
for current_from=1:9
    for current_to=1:10
        Best2(current_from,current_to) = Best((current_from-1)*(10)+current_to);
    end
end
        
debug = p.*r;
debug2 = sum(debug,2);        
        
        
        