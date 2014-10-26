clear all;
close all;
clc;


col = ['r','b','g','c','y','k'];
mar = ['o','+','s','d','.','*'];
 query = {'read','scan','join'};

for i = 2
    fn = strcat('result_',query{i},'.txt');
    m = load(fn);

    plot(m(:,1),m(:,2),'color',col(i),'marker',mar(i),'linewidth',1.5);
    hold on;
end
legend(query);
title('RMSE vs. Sample Size','fontsize',18)
xlabel('Sample Size','fontsize',14);
ylabel('Error','fontsize',14);
hold off;
set(gca,'FontSize',14);
hold off;
saveas(gca, 'result.eps','epsc');