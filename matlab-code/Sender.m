function flag=Sender(testimg,respath,keypath,addpath) %

%bianhao  = 15;
%caozuo=1;
%src =[testimg, num2str(bianhao) ,'.jpg'];
src=testimg;
img=imread(src);%read image
%testv2 = img;
[y1,x1,kuan,gao] = LabelBox(src); %Select the processing area��label the image location
imgEmb=img;
imgb=double(img(:,:,3));
imgg=double(img(:,:,2));
imgr=double(img(:,:,1));
w = fspecial('gaussian',[3,3],0.3); %gaussian��3*3��StandardDeviation=0.3
embeddingnum=0; 

%Ƕ���㷨
g=double(img);   
T=1;
b=1;%������Ϣ����
cz=kuan; %�ؼ�������
cy = gao; %�ؼ�����߶�
count=30; %ѭ������  ѭ�������͸�˹�˲����˲���ϵ��������Ƭ�����Ȳ�ͬ��Ҫ���ϵ�����
con1=rand(1,cz*cy);  
con=int8(round(con1));
%econ=fliplr(con);
%ִ��Ƕ��
for a = 1 :3
    ft=zeros([count,cz,cy]);%��������
    jo=zeros([count,cz,cy]);
    %���Ʋ������� ����Ϊ�ؼ�������*����
   
if(a == 1)
    g =imgb;end
if(a == 2)
    g = imgg;end
if(a == 3)
    g = imgr;end      
for p=1:count
    for i=x1+1: x1+gao
        iii=i-(x1+1)+1;
        for j= y1+1 : y1+kuan
%             disp("i=");
%             disp(i);
%             disp("j=");
%             disp(j);
            jjj= j-(y1+1)+1;
        %Ԥ���ֵ
        pre=predict(i+1,j+1,g);%Ԥ��
        delta=g(i+1,j+1)-double(pre); 
        f=g(i+1,j+1);
        g(i+1,j+1)=pre;
        change=0;
        for k=0:2
            for m=0:2
               change=change+(g(i+k,j+m)*w(1+k,1+m));  %�˲���ֵ
            end
        end
        lx=fix(change)-g(i+1,j+1);
        if(delta<=T&&delta>=(-T)) 
            delta=(delta*2)+b;       
                    delta=delta+double(con(1,(i-(x1+1))*kuan+(j-(y1+1))+1));
                    yu=mod(delta,2);
                    jo(p,iii,jjj)=yu;
                    delta1=fix(delta/2);
                    if(double(pre)+delta1+lx<0||double(pre)+delta1+lx>255||delta==-1||delta==0||delta==1) %���
                        ft(p,iii,jjj)=1;g(i+1,j+1)=f;   
                    else
                        g(i+1,j+1)=double(pre)+delta1+lx;
                        embeddingnum=embeddingnum+1;  
                    end
        elseif(delta<(-T))
            delta=delta-T;   %-1
                 delta=delta+double(con(1,(i-(x1+1))*kuan+(j-(y1+1))+1));
                    yu=mod(delta,2);
                    jo(p,iii,jjj)=yu;
                    delta1=fix(delta/2);
                    if(double(pre)+delta1+lx<0||double(pre)+delta1+lx>255||delta==-1||delta==0||delta==1) %���
                        ft(p,iii,jjj)=1;g(i+1,j+1)=f;   
                    else
                        g(i+1,j+1)=double(pre)+delta1+lx;
                    end
        else  %delta>TH      
            delta=delta+T+1;
               delta=delta+double(con(1,(i-(x1+1))*kuan+(j-(y1+1))+1));
                    yu=mod(delta,2);
                    jo(p,iii,jjj)=yu;
                    delta1=fix(delta/2);
                    if(double(pre)+delta1+lx<0||double(pre)+delta1+lx>255||delta==-1||delta==0||delta==1) %���
                        ft(p,iii,jjj)=1;g(i+1,j+1)=f;   
                    else
                        g(i+1,j+1)=double(pre)+delta1+lx;
                    end
        end
        end
    end
end

if(a == 1)
    imgEmb(:,:,3)=g;
    ftb = ft;
    job = jo;
    %econb = econ;
end
if(a == 2)
    imgEmb(:,:,2)=g;
    ftg = ft;
    jog = jo;
    %econg = econ;
end
if(a == 3)
   imgEmb(:,:,1)=g;
   ftr = ft;
   jor = jo;
   %econr = econ;
end  
end


resimg=[respath, 'face.bmp'];  %�����ļ�
imwrite(uint8(imgEmb),resimg);

%subplot(1,2,1),imshow(img),title('ԭʼͼ��'); 
%subplot(1,2,2),imshow(uint8(imgEmb)),title('Я��������Ϣ���ܱ���������ͼ��'); 
disp("Ƕ����ɣ�");

addtion = [addpath,'face.mat'];
%save(addtion,y1,x1,kuan,gao,count,T,ftb,job,econb,ftg,jog,econg,ftr,jor,econr);
save(addtion,'y1','x1','kuan','gao','count','T','ftb','job','ftg','jog','ftr','jor');
fid =fopen(keypath,"w");
fprintf(fid,'%f\t',con1);
fclose(fid);
flag=1;
%save addrgb.mat ftb job econb ftg jog econg ftr jor econr;
% testv2=imcrop(imgEmb,[y1,x1,kuan,gao]);
% imshow(testv2);  
%imgEmb=uint8(g); 