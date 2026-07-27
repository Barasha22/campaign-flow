ALTER TABLE campaigns
ADD COLUMN recipient_list_id UUID;

ALTER TABLE campaigns
ADD CONSTRAINT fk_campaigns_recipient_list
FOREIGN KEY (recipient_list_id)
REFERENCES recipient_lists(id);

CREATE INDEX idx_campaigns_recipient_list_id
ON campaigns(recipient_list_id);