clear all;
% close all;
clc;


figure(5);

n = 5;

latency = zeros(n,10);
for i = 1:10
    fn = strcat('scan_',num2str(i),'_sameData.txt');
%      fn = strcat('scan_30_',num2str(i),'_trainData.txt');

    m = load(fn);
    
    disp(m);
    for j = 1:n
        latency(j,i) = m(j,1);
    end


end

disp(latency);
col = ['r','b','g','c','k'];
mar = ['o','+','s','d','.'];
%leng = {'numRows','numCol','rowkeySize','colSize','retSize'};
% leng = leng(1:nCol);
for i =0:n-1
    plot(latency(i+1,:),'color',col(mod(i,5)+1),'marker',mar(mod(i,5)+1));
    hold on;

end
hold off;
title('Test Data','FontSize',18);
xlabel('nth run','FontSize',14);
ylabel('Latency (ms)','FontSize',14);
legend('table 1','table 2','table 3','table 4','table 5','FontSize',14);
% hold off

